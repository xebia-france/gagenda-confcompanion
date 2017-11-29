import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import org.apache.commons.codec.digest.DigestUtils

private const val SPEAKERS_GOOGLE_KEY = "attendees"

val Event.speakers: List<Speaker>?
    get() {
        if (!containsKey(SPEAKERS_GOOGLE_KEY)) {
            return null
        }

        val speakers: MutableList<Speaker> = mutableListOf()

        try {
            (get(SPEAKERS_GOOGLE_KEY) as ArrayList<EventAttendee>)
                    .forEach {
                        speakers.add(Speaker(it.generateSpeakerId(), it.displayName, null, it.generatePictureUrl()))
                    }
        } catch (ignored: Exception) {
            // Speaker may have no displayName
        }

        if (isEmpty()) {
            return null
        } else {
            return speakers
        }
    }

private fun EventAttendee.generatePictureUrl(): String {
    return "https://www.gravatar.com/avatar/${DigestUtils.md5Hex(email.toLowerCase().trim())}"
}

private fun EventAttendee.generateSpeakerId(): String {
    return email.replace(".", "").substring(0, email.indexOf("@"))
}
