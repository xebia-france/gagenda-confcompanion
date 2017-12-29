import com.google.api.services.calendar.model.Event
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.threeten.extra.Interval
import java.text.SimpleDateFormat
import java.util.*

val rooms = listOf(
        "Eiffel",
        "Montmartre",
        "Monceau",
        "3ème",
        "Studio"
)

class TalkService(val conferenceId: String) {

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
            talks.add(Talk(conferenceId,
                    Date(event.start.dateTime.value),
                    "xke-${index + 1}", Date(event.end.dateTime.value),
                    event.summary,
                    event.speakers,
                    event.description,
                    event.location))
        }
        return talks
    }

    fun toJson(talks: List<Talk>): String = moshiTypeAdapter.toJson(talks)

    fun computeRooms(talks: List<Talk>): List<Talk> {
        val talksSuscriptions = FirebaseFetcher()
                .fetch("subscription/$conferenceId")
                .toList()
                .blockingGet()

        talks.filter {
            it.kind == "keynote" && it.room == null
        }.forEach {
            it.room = rooms.first()
        }

        //only assign rooms to non assigned rooms
        var talksToModify = talks.filter {
            it.room == null
        }

        //set keynote rooms to biggest
        talksToModify.filter {
            it.kind == "keynote"
        }.forEach {
            it.room = rooms.first()
        }

        //order talks by attendants
        talksToModify = talksToModify.sortedBy { talk ->
            val c = talksSuscriptions.find {
                it.first == talk.id
            }?.second

            if (c == null) {
                0
            } else {
                c
            }
        }

        //reverse list to get biggest talks first
        talksToModify = talksToModify.reverseList()


        //assign rooms
        talksToModify.forEach { talk ->
            val currentTalkInterval = Interval.of(talk.fromTime.toInstant(), talk.toTime.toInstant())

            //check freeRooms during talk
            val freeRooms = talks.filter { t ->
                val otherInterval = Interval.of(t.fromTime.toInstant(), t.toTime.toInstant())
                currentTalkInterval.overlaps(otherInterval)
            }.filter {
                it.room != null
            }.map {
                it.room
            }

            //assign first empty room during talk using biggest one first
            talk.room = rooms.firstOrNull {
                !freeRooms.contains(it)
            }
        }

        //add back filtered talks to the list
        val newTalks = mutableListOf<Talk>()
        newTalks.addAll(talksToModify)
        talks.filter { talk ->
            talksToModify.count {
                it.id == talk.id
            } == 0
        }.forEach {
            newTalks.add(it)
        }

        //return room ordered events
        return newTalks.sortedBy {
            rooms.indexOf(it.room)
        }
    }
}

private fun <E> List<E>.reverseList(): List<E> {
    val newList = mutableListOf<E>()

    newList.addAll(this)
    newList.reverse()

    return newList
}

class FirebaseFetcher {
    init {
        val stream = TalkService::class.java.getResourceAsStream("credentials/firebase_adminsdk.json")

        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(stream))
                .setDatabaseUrl("https://conf-companion.firebaseio.com/")
                .build()

        FirebaseApp.initializeApp(options)
    }

    fun fetch(node: String): Observable<Pair<String, Long>> {
        val ps = PublishSubject.create<Pair<String, Long>>()

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(node)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ref.removeEventListener(this)

                dataSnapshot.children.forEach {
                    val post = Pair(it.key, it.childrenCount)
                    ps.onNext(post)
                }

                ps.onComplete()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        return ps
    }
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