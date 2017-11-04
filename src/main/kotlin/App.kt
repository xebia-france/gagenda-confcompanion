import com.google.api.client.util.DateTime
import mu.KotlinLogging
import java.io.File
import java.util.*

private val LOG = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val from: Calendar = Calendar.getInstance()
    from.set(2017, 10, 6, 7, 0)

    val to: Calendar = Calendar.getInstance()
    to.time = from.time
    to.add(Calendar.HOUR, 10)

    val agendaService = GoogleAgendaService()

    val events = agendaService.getEvents("xebia.fr_sh679blpn2vkmhk7i1rdllo3t0@group.calendar.google.com",
            DateTime(from.time), DateTime(to.time))

    if (events.isEmpty()) {
        LOG.debug { "Sorry, but no events found :(" }
    } else {
        val talkService = TalkService()
        val talks = talkService.convert(events)
        val json = talkService.toJson(talks)
        LOG.debug { json }
        File("./schedule.json").bufferedWriter().use {
            it.write(json)
        }
    }
}