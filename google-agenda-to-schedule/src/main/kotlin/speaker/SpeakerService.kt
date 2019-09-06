package speaker

import talk.DateJsonAdapter
import talk.Talk
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.apache.commons.codec.digest.DigestUtils


class SpeakerService {
  private val type = Types.newParameterizedType(List::class.java, Speaker::class.java)
  private val moshi by lazy {
    Moshi.Builder().add(DateJsonAdapter()).build()
  }
  private val moshiTypeAdapter by lazy {
    moshi.adapter<List<Speaker>>(type)
  }

  fun convert(events: List<Talk>): List<Speaker> {
    val speakers = mutableSetOf<Speaker>()
    events.forEach { talk ->
      talk.speakers?.forEach { speakerTalk ->
        var speaker = speakers.find { s -> s.id == speakerTalk.id }
        if (speaker == null) {
          speaker = Speaker(
            id = speakerTalk.id,
            firstName = speakerTalk.name,
            imageURL = "https://www.gravatar.com/avatar/${speakerTalk.id.hash()}?s=400"
          )
          speakers.add(speaker)
        }
        speaker.talks.add(talk)
      }
    }
    return speakers.toList()
  }

  fun toJson(talks: List<Speaker>): String = moshiTypeAdapter.toJson(talks)
}

private fun String.hash() = DigestUtils.md5Hex(this)
