import com.google.api.client.util.DateTime
import mu.KotlinLogging
import java.io.File
import java.util.*

private val LOG = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val from: Calendar = Calendar.getInstance()
    from.set(2018, 0, 8, 8, 0)

    val to: Calendar = Calendar.getInstance()
    to.time = from.time
    to.add(Calendar.HOUR, 12)

    val agendaService = GoogleAgendaService()

    val events = agendaService.getEvents("xebia.fr_sh679blpn2vkmhk7i1rdllo3t0@group.calendar.google.com",
            DateTime(from.time), DateTime(to.time))
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

        val talkService = TalkService()
        val talks = talkService.convert(events)
        val scheduleJson = talkService.toJson(talks)

        File("./schedule.json").bufferedWriter().use {
            it.write(scheduleJson)
        }
        AWSS3Persister().putSchedule("./schedule.json")


        val speakerService = SpeakerService()
        val speakersJson = speakerService.toJson(speakerService.convert(talks))

        File("./speakers.json").bufferedWriter().use {
            it.write(speakersJson)
        }
        AWSS3Persister().putSpeakers("./speakers.json")
    }
}