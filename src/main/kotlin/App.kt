import com.google.api.client.util.DateTime
import com.xenomachina.argparser.ArgParser
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import org.jsoup.safety.Whitelist

const val DATE_FORMAT = "yyyyMMdd"

private val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT)
private val LOG = KotlinLogging.logger {}

fun main(args: Array<String>) {
    ArgParser(args).parseInto(::AppArgParser).run {
        LOG.debug { "$rooms $calendar $from $duration" }
        compute(rooms, calendar, DATE_FORMATTER.parse(from), duration.toInt())
    }
}

private fun br2nl(html: String?): String? {
    if (html == null)
        return html
    val document = Jsoup.parse(html)
    document.outputSettings(Document.OutputSettings().prettyPrint(false))//makes html() preserve linebreaks and spacing
    document.select("br").append("\\n")
    document.select("p").prepend("\\n\\n")
    val s = document.html().replace("\\\\n".toRegex(), "\n")
    return Jsoup.clean(s, "", Whitelist.none(), Document.OutputSettings().prettyPrint(false))
}

fun compute(computeRooms: Boolean, calendarId: String, fromDay: Date, durationInDay: Int) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))

    val from = Calendar.getInstance()
    from.time = fromDay
    from.set(Calendar.HOUR, 0)
    from.set(Calendar.MINUTE, 1)

    val to = Calendar.getInstance()
    to.time = from.time
    to.add(Calendar.DATE, durationInDay)
    to.set(Calendar.HOUR, 23)
    to.set(Calendar.MINUTE, 59)

    val agendaService = GoogleAgendaService()

    val events = agendaService.getEvents(calendarId,
            DateTime(from.time), DateTime(to.time))
            .filter { event ->
                if (event.summary == "Formation newcomer") {
                    return@filter false
                }
                return@filter true
            }
            .filter { event ->
                event.attendees?.forEach {
                    if (it.email == "allfrance@xebia.fr") {
                        return@filter false
                    }
                }
                return@filter true
            }.map {
                it.description = br2nl(it.description)
                it
            }

    if (events.isEmpty()) {
        LOG.debug { "Sorry, but no events found :(" }
    } else {
        val talkService = TalkService(from.generateId("xke"))
        var talks = talkService.convert(events)

        if (computeRooms) {
            talks = talkService.computeRooms(talks)
        }

        val scheduleJson = talkService.toJson(talks)

        File("build/schedule.json").bufferedWriter().use {
            it.write(scheduleJson)
        }
        //AWSS3Persister().putSchedule("build/schedule.json")


        val speakerService = SpeakerService()
        val speakersJson = speakerService.toJson(speakerService.convert(talks))

        File("build/speakers.json").bufferedWriter().use {
            it.write(speakersJson)
        }
        //AWSS3Persister().putSpeakers("build/speakers.json")
    }
}

private fun Calendar.generateId(slug: String): String {
    val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.FRANCE)

    return "$slug-${dateFormatter.format(this.time)}"
}
