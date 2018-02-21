import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee

private const val SPEAKERS_GOOGLE_KEY = "attendees"

val Event.speakers: List<SpeakerTalk>
    get() {
        val speakers: MutableList<SpeakerTalk> = mutableListOf()

        try {
            (get(SPEAKERS_GOOGLE_KEY) as ArrayList<EventAttendee>)
                    .forEach {
                        val speaker = SpeakerFetcher.fetch(it.generateSpeakerId())

                        speakers.add(SpeakerTalk(speaker.id, speaker.firstName))
                    }
        } catch (ignored: Exception) {
            // Speaker may have no displayName
        }

        return speakers
    }

private fun EventAttendee.generateSpeakerId(): String {
    return email.replace(".", "").substring(0, email.indexOf("@"))
}