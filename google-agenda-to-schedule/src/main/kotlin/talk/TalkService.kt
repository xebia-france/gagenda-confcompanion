package talk

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
import agenda.speakers
import java.text.SimpleDateFormat
import java.util.*

val rooms = listOf(
  "Auditorium",
  "Avenir Light + Book",
  "Avenir Roman + Medium",
  "Avenir Black"
)

class TalkService(private val conferenceId: String) {

  private val type = Types.newParameterizedType(List::class.java, Talk::class.java)
  private val moshi by lazy {
    Moshi.Builder().add(DateJsonAdapter()).build()
  }
  private val moshiTypeAdapter by lazy {
    moshi.adapter<List<Talk>>(type)
  }

  fun convert(events: List<Event>, computeSpeaker: Boolean): List<Talk> {
    val talks = mutableListOf<Talk>()
    events.forEachIndexed { _, event ->
      if (computeSpeaker) {
        talks.add(Talk(conferenceId,
          Date(event.start.dateTime.value),
          "cc-${event.id}", Date(event.end.dateTime.value),
          event.summary,
          event.speakers,
          event.description,
          event.location,
          event.hangoutLink))
      } else {
        talks.add(Talk(conferenceId,
          Date(event.start.dateTime.value),
          "cc-${event.id}", Date(event.end.dateTime.value),
          event.summary,
          listOf(),
          event.description,
          event.location,
          event.hangoutLink))
      }
    }
    return talks
  }

  fun toJson(talks: List<Talk>): String = moshiTypeAdapter.toJson(talks)

  fun computeRooms(talks: List<Talk>): List<Talk> {
    val talksSuscriptions = FirebaseFetcher()
      .fetch("subscription/$conferenceId")
      .toList()
      .blockingGet()

    //for all keynote assign biggest room, except fondations
    talks.filter {
      (it.kind == "keynote" && it.room == null) && it.title.toLowerCase() != "fondations"
    }.forEach {
      it.room = rooms.first()
    }

    //only assign talk.getRooms to non assigned talk.getRooms, except foundations
    var talksToModify = talks.filter {
      it.room == null && it.title.toLowerCase() != "fondations"
    }

    //set keynote talk.getRooms to biggest
    talksToModify.filter {
      it.kind == "keynote"
    }.forEach {
      it.room = rooms.first()
    }

    //order talks by attendants
    talksToModify = talksToModify.sortedBy { talk ->

      talksSuscriptions.find {
        it.first == talk.id
      }?.second ?: 0
    }

    //reverse list to get biggest talks first
    talksToModify = talksToModify.reverseList()


    //assign talk.getRooms
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
    val stream = TalkService::class.java.getResourceAsStream("/credentials/firebase_adminsdk.json")

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
