import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup


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
            talk.speakers?.forEach {
                if (speakers.count { speaker ->
                    it.id == speaker.id
                } == 0) {
                    speakers.add(convert(it))
                }
                speakers.filter { speaker -> it.id == speaker.id }
                        .forEach {
                            it.talks.add(talk)
                        }
            }
        }

        return speakers.toList()
    }

    private fun convert(speakerTalk: SpeakerTalk): Speaker {
        val speaker = Speaker(speakerTalk.id, speakerTalk.name)

        speaker.imageURL = generatePictureUrl("${speakerTalk.id}@xebia.fr")

        val xebiaUrl = "https://blog.xebia.fr/author/${speakerTalk.id}"

        try {
            val (_, _, result) = xebiaUrl.httpGet().responseString()

            when (result) {
                is Result.Success -> {
                    val data = result.get()

                    val doc = Jsoup.parse(data)
                    doc.head().getElementsByAttributeValueStarting("property", "profile:")
                            .forEach {
                                when (it.attr("property")) {
                                    "profile:first_name" -> {
                                        speaker.firstName = it.attr("content")
                                    }
                                    "profile:last_name" -> {
                                        speaker.lastName = it.attr("content")
                                    }
                                }
                            }

                    doc.body().getElementsByClass("description")
                            .firstOrNull()?.let {
                        speaker.bio = it.html()
                    }
                }
            }
            return speaker
        } catch (e: Exception) {
            return speaker
        }
    }

    fun toJson(talks: List<Speaker>): String = moshiTypeAdapter.toJson(talks)

    private fun generatePictureUrl(input: String): String = "https://www.gravatar.com/avatar/${DigestUtils.md5Hex(input.toLowerCase().trim())}?s=400"
}