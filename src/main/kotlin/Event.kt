import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee

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
                        speakers.add(Speaker(it.displayName))
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