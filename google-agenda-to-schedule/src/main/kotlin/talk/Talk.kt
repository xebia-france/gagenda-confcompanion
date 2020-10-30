package talk

import speaker.SpeakerTalk
import java.util.*

val HANDS_ON_PATTERN = ".*(hands-on|handson|codelab|hands'on|hands on).*".toPattern()
val WORKSHOP_PATTERN = ".*(workshop).*".toPattern()
val KEYNOTE_PATTERN = ".*(plénière|annonce|^fondations|new comers|newcomers|keynote).*".toPattern()
val CLOSING_PATTERN = ".*(^closing|clôture$).*".toPattern()
val PARTY_PATTERN = ".*(frenchkit party|evening party|after ked|^cocktail|tisanes).*".toPattern()
val LUNCH_PATTERN = ".*(lunch|déjeuner).*".toPattern()
val OPENING_PATTERN = ".*(^welcoming|^opening|opening$|^accueil|mot des organisateurs).*".toPattern()
val BREAKFAST_PATTERN = ".*(breakfast).*".toPattern()
val BREAK_PATTERN = ".*(meet and greet|afternoon break|morning break|^break$|^pause|^encas).*".toPattern()

val KIND_KEYNOTE_PATTERN = listOf(
  KEYNOTE_PATTERN,
  CLOSING_PATTERN,
  PARTY_PATTERN,
  LUNCH_PATTERN,
  OPENING_PATTERN,
  BREAKFAST_PATTERN,
  BREAK_PATTERN).joinToString("|").toPattern()

const val HANDSON = "handson"
const val WORKSHOP = "workshop"
const val CLOSING = "closing"
const val PARTY = "party"
const val LUNCH = "lunch"
const val OPENING = "opening"
const val BREAKFAST = "breakfast"
const val BREAK = "break"
const val KEYNOTE = "keynote"
const val TALK = "talk"

const val REX = "rex"
const val DEVOPS = "devops"
const val FRONT = "front"
const val AGILE = "agile"
const val BACK = "back"
const val DATA = "data"
const val MOBILE = "mobile"
const val CLOUD = "cloud"
const val IOT = "iot"
const val CRAFT = "craft"

const val DATA_INTIMACY = "DataIntimacy"
const val REACTIVE_FIRST = "ReactiveFirst"
const val ML_DONE_RIGHT = "MLDoneRight"

data class Talk(val conferenceId: String,
                val fromTime: Date,
                val id: String,
                val toTime: Date,
                val title: String,
                val speakers: List<SpeakerTalk>?,
                val summary: String?,
                var room: String? = null,
                val conferenceUrl: String? = null) {

    var type: String? = null
    var kind: String? = null
    var track: String? = null

    init {
        val lowerCaseTitle = title.toLowerCase()
        type = when {
            HANDS_ON_PATTERN.matcher(lowerCaseTitle).matches() -> HANDSON
            WORKSHOP_PATTERN.matcher(lowerCaseTitle).matches() -> WORKSHOP
            CLOSING_PATTERN.matcher(lowerCaseTitle).matches() -> CLOSING
            PARTY_PATTERN.matcher(lowerCaseTitle).matches() -> PARTY
            LUNCH_PATTERN.matcher(lowerCaseTitle).matches() -> LUNCH
            OPENING_PATTERN.matcher(lowerCaseTitle).matches() -> OPENING
            BREAKFAST_PATTERN.matcher(lowerCaseTitle).matches() -> BREAKFAST
            BREAK_PATTERN.matcher(lowerCaseTitle).matches() -> BREAK
            KEYNOTE_PATTERN.matcher(lowerCaseTitle).matches() -> KEYNOTE
            else -> TALK
        }

        if (KIND_KEYNOTE_PATTERN.matcher(lowerCaseTitle).matches()) {
            kind = KEYNOTE
        }

        if (summary != null) {
            if (summary.contains("#$REX", true)) type = REX
            if (summary.contains("#$HANDSON", true)) type = HANDSON
            if (summary.contains("#$WORKSHOP", true)) type = WORKSHOP
            if (summary.contains("#$DEVOPS", true)) track = DEVOPS
            if (summary.contains("#$FRONT", true)) track = FRONT
            if (summary.contains("#$AGILE", true)) track = AGILE
            if (summary.contains("#$BACK", true)) track = BACK
            if (summary.contains("#$DATA", true)) track = DATA
            if (summary.contains("#$MOBILE", true)) track = MOBILE
            if (summary.contains("#$CLOUD", true)) track = CLOUD
            if (summary.contains("#$IOT", true)) track = IOT
            if (summary.contains("#$CRAFT", true)) track = CRAFT
            // DataXDay 2019
            if (summary.contains("#$DATA_INTIMACY", true)) track = DATA_INTIMACY
            if (summary.contains("#$REACTIVE_FIRST", true)) track = REACTIVE_FIRST
            if (summary.contains("#$ML_DONE_RIGHT", true)) track = ML_DONE_RIGHT
        }
    }
}
