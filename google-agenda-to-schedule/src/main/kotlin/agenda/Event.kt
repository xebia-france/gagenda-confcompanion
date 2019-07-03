package agenda

import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import speaker.SpeakerTalk

const val SPEAKERS_GOOGLE_KEY = "attendees"
const val EMAIL_SEPARATOR = "@"

val Event.speakers: List<SpeakerTalk>
  get() {
    val speakers: MutableList<SpeakerTalk> = mutableListOf()
    try {
      (get(SPEAKERS_GOOGLE_KEY) as List<*>)
        .forEach { attendee ->
          when (attendee) {
            is EventAttendee -> speakers.add(SpeakerTalk(attendee.email, attendee.name()))
          }
        }
    } catch (ignored: Exception) {
      // agenda.getSpeakers.Speaker may have no displayName
    }
    return speakers
  }

private fun EventAttendee.name() = displayName ?: email.split(EMAIL_SEPARATOR)[0]
