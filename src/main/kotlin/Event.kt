import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee

private const val SPEAKERS_GOOGLE_KEY = "attendees"

val Event.speakers: List<SpeakerTalk>
    get() {
        val speakers: MutableList<SpeakerTalk> = mutableListOf()

        try {
            (get(SPEAKERS_GOOGLE_KEY) as ArrayList<EventAttendee>)
                    .forEach {
                        if (it.displayName != null) {
                            speakers.add(SpeakerTalk(it.generateSpeakerId(), it.displayName))
                        } else {
                            speakers.add(SpeakerTalk(it.generateSpeakerId(), it.generateSpeakerId()))
                        }
                    }
        } catch (ignored: Exception) {
            // Speaker may have no displayName
        }

        return speakers
    }

private fun EventAttendee.generateSpeakerId(): String {
    return email.replace(".", "").substring(0, email.indexOf("@"))
}
