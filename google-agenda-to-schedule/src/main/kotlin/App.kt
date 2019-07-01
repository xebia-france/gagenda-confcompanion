import com.google.api.client.util.DateTime
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.io.File
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "yyyyMMdd"

private val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT)
private val LOG = KotlinLogging.logger {}
private val OUTPUT_DIR = Paths.get("build").toAbsolutePath()

fun main() {
    val calendarId: String = System.getenv("CALENDAR_ID")
    val dayFrom: String = System.getenv("DAY_FROM")
    val duration: String = System.getenv("DURATION")
    val room: String = System.getenv("ROOM")
    val s3dir: String = System.getenv("S3_DIR")
    val speaker: String = System.getenv("SPEAKER")
    val local: String = System.getenv("LOCAL") ?: "false"

    LOG.debug {
        """
    CalendarId: $calendarId
    FromDay: $dayFrom
    DurationInDays: $duration
    ComputeRoom: $room
    Local (no S3 upload): $local"""
    }

    val store = if (local.toBoolean()) NoStore() else AwsS3Store()

    compute(room.toBoolean(), calendarId, DATE_FORMATTER.parse(dayFrom), duration.toInt(), s3dir, speaker.toBoolean(), store)
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

fun compute(computeRooms: Boolean,
            calendarId: String,
            fromDay: Date,
            durationInDay: Int,
            s3dir: String,
            computeSpeaker: Boolean,
            store: Store) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))

    val from = Calendar.getInstance()
    from.time = fromDay
    from.set(Calendar.HOUR, 0)
    from.set(Calendar.MINUTE, 1)


    val to = Calendar.getInstance()
    to.time = from.time
    to.add(Calendar.DATE, durationInDay - 1)
    to.set(Calendar.HOUR, 23)
    to.set(Calendar.MINUTE, 59)

    LOG.debug { "From ${from.time} to ${to.time}" }

    val agendaService = GoogleAgendaService()

    val events = agendaService.getEvents(calendarId,
            DateTime(from.time), DateTime(to.time))
            .asSequence()
            .filter { event ->
                if (event.summary == "Formation newcomer") {
                    return@filter false
                }
                return@filter true
            }
            .filter { event -> event.summary != "XKE" }
            .filter { event ->
                event.attendees?.forEach {
                    if (it.email == "allfrance@xebia.fr") {
                        return@filter false
                    }
                }
                return@filter true
            }
            .filter { event -> !event.summary.startsWith("DataXDay") }
            .map {
                it.description = br2nl(it.description)
                it
            }
            .toList()

    if (events.isEmpty()) {
        LOG.debug { "Sorry, but no events found :(" }
    } else {
        val talkService = TalkService(from.generateId("cc"))
        var talks = talkService.convert(events, computeSpeaker)

        if (computeRooms) {
            talks = talkService.computeRooms(talks)
        }

        talks = talks.sortedBy { event -> event.room }

        val scheduleJson = talkService.toJson(talks)

        File("$OUTPUT_DIR/schedule.json").bufferedWriter().use {
            it.write(scheduleJson)
        }
        store.putSchedule(s3dir, "build/schedule.json")

        LOG.info { "$OUTPUT_DIR/schedule.json" }

//        if (computeSpeaker) {
//            val speakerService = SpeakerService()
//            val speakersJson = speakerService.toJson(speakerService.convert(talks))
//
//            File("$OUTPUT_DIR/speakers.json").bufferedWriter().use {
//                it.write(speakersJson)
//            }
//            store.putSpeakers(s3dir, "build/speakers.json")
//
//            LOG.info { "$OUTPUT_DIR/speakers.json" }
//        }
    }
}

private fun Calendar.generateId(slug: String): String {
    val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.FRANCE)

    return "$slug-${dateFormatter.format(this.time)}"
}
