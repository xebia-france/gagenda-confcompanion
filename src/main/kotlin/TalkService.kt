import com.google.api.services.calendar.model.Event
import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*

class TalkService {

    private val type = Types.newParameterizedType(List::class.java, Talk::class.java)
    private val moshi by lazy {
        Moshi.Builder().add(DateJsonAdapter()).build()
    }
    private val moshiTypeAdapter by lazy {
        moshi.adapter<List<Talk>>(type)
    }

    fun convert(events: List<Event>): List<Talk> {
        val talks = mutableListOf<Talk>()
        events.forEachIndexed { index, event ->
            talks.add(Talk(Date(event.start.dateTime.value),
                    "id-$index", Date(event.end.dateTime.value),
                    event.summary))
        }
        return talks
    }

    fun toJson(talks: List<Talk>): String = moshiTypeAdapter.toJson(talks)
}

class DateJsonAdapter {
    companion object {
        val DATE_FORMATTER: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE)
    }

    @ToJson
    fun toJson(date: Date): String = DATE_FORMATTER.format(date)

    @FromJson
    fun fromJson(date: String): Date = DATE_FORMATTER.parse(date)
}