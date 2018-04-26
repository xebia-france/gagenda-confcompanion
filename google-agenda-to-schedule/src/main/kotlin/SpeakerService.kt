import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


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
        return SpeakerFetcher.fetch(speakerTalk.id)
    }


    fun toJson(talks: List<Speaker>): String = moshiTypeAdapter.toJson(talks)
}