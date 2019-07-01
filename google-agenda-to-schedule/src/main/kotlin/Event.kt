import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee

private const val SPEAKERS_GOOGLE_KEY = "attendees"

val Event.speakers: List<SpeakerTalk>
    get() {
        val speakers: MutableList<SpeakerTalk> = mutableListOf()
        try {
            (get(SPEAKERS_GOOGLE_KEY) as ArrayList<EventAttendee>)
                    .forEach {
                        speakers.add(SpeakerTalk(it.generateSpeakerId(), it.displayName ?: it.email.split("@")[0]))
                    }
        } catch (ignored: Exception) {
            // Speaker may have no displayName
        }
        return speakers
    }

private fun EventAttendee.generateSpeakerId(): String {
    return email.replace(".", "").substring(0, email.indexOf("@"))
}
