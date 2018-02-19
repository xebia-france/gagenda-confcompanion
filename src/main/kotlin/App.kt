import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.api.client.util.DateTime
import mu.KotlinLogging
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private val LOG = KotlinLogging.logger {}

class Data {
    var queryStringParameters: QueryStringParams? = QueryStringParams()

    class QueryStringParams {
        var computeRooms: Boolean = false
    }
}

class AppHandler : RequestHandler<Data, String> {
    override fun handleRequest(input: Data, context: Context?): String {
        if (input.queryStringParameters != null) {
            println("Compute with computeRooms = ${input.queryStringParameters!!.computeRooms}")
            compute(input.queryStringParameters!!.computeRooms)
        } else {
            println("Compute with computeRooms = false (no arg provided)")
            compute(false)
        }
        return "done"
    }
}

fun main(args: Array<String>) {
    compute(true)
}

fun compute(computeRooms: Boolean? = false) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))

    val from: Calendar = Calendar.getInstance()
    from.set(2018, 2, 12, 8, 0)

    val to: Calendar = Calendar.getInstance()
    to.time = from.time
    to.add(Calendar.HOUR, 12)

    val agendaService = GoogleAgendaService()

    val events = agendaService.getEvents("xebia.fr_sh679blpn2vkmhk7i1rdllo3t0@group.calendar.google.com",
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
            }

    if (events.isEmpty()) {
        LOG.debug { "Sorry, but no events found :(" }
    } else {
        val talkService = TalkService(from.generateId("xke"))
        var talks = talkService.convert(events)

        if (computeRooms == true) {
            talks = talkService.computeRooms(talks)
        }

        val scheduleJson = talkService.toJson(talks)

        File("/tmp/schedule.json").bufferedWriter().use {
            it.write(scheduleJson)
        }
        AWSS3Persister().putSchedule("/tmp/schedule.json")


        val speakerService = SpeakerService()
        val speakersJson = speakerService.toJson(speakerService.convert(talks))

        File("/tmp/speakers.json").bufferedWriter().use {
            it.write(speakersJson)
        }
        AWSS3Persister().putSpeakers("/tmp/speakers.json")
    }
}

private fun Calendar.generateId(slug: String): String {
    val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.FRANCE)

    return "$slug-${dateFormatter.format(this.time)}"
}
