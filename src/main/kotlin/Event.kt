import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import org.apache.commons.codec.digest.DigestUtils

private const val SPEAKERS_GOOGLE_KEY = "attendees"

val Event.speakers: List<SpeakerTalk>
    get() {
        val speakers: MutableList<SpeakerTalk> = mutableListOf()

        try {
            (get(SPEAKERS_GOOGLE_KEY) as ArrayList<EventAttendee>)
                    .forEach {
                        speakers.add(SpeakerTalk(it.generateSpeakerId(), it.displayName))
                    }
        } catch (ignored: Exception) {
            // Speaker may have no displayName
        }

        return speakers
    }

private fun EventAttendee.generatePictureUrl(): String {
    return "https://www.gravatar.com/avatar/${DigestUtils.md5Hex(email.toLowerCase().trim())}"
}

private fun EventAttendee.generateSpeakerId(): String {
    return email.replace(".", "").substring(0, email.indexOf("@"))
}
