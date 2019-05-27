import java.util.*
import java.util.regex.Pattern

data class Talk(val conferenceId: String,
                val fromTime: Date,
                val id: String,
                val toTime: Date,
                var title: String,
                val speakers: List<SpeakerTalk>? = null,
                val summary: String?,
                var room: String?) {

    var type: String = ""
    var kind: String? = null
    var track: String? = null

    init {
        type = when {
            Pattern.compile(".*(handson|codelab|hands'on|hands on).*").matcher(title.toLowerCase()).matches() -> "hands'on"
            Pattern.compile(".*(workshop).*").matcher(title.toLowerCase()).matches() -> "workshop"
            else -> "talk"
        }

        if (Pattern.compile(".*(^closing|clôture$).*").matcher(title.toLowerCase()).matches()) {
            type = "closing"
            kind = "keynote"
        }

        if (Pattern.compile(".*(evening party|after xke$|^cocktail|tisanes).*").matcher(title.toLowerCase()).matches()) {
            type = "party"
            kind = "keynote"
        }

        if (Pattern.compile(".*(lunch|déjeuner).*").matcher(title.toLowerCase()).matches()) {
            type = "lunch"
            kind = "keynote"
        }

        if (Pattern.compile(".*(^welcoming|^opening|opening$|^accueil|mot des organisateurs).*").matcher(title.toLowerCase()).matches()) {
            type = "opening"
            kind = "keynote"
        }

        if (Pattern.compile(".*(breakfast).*").matcher(title.toLowerCase()).matches()) {
            type = "breakfast"
            kind = "keynote"
        }

        if (Pattern.compile(".*(meet and greet|afternoon break|morning break|^break$|^pause|^encas).*").matcher(title.toLowerCase()).matches()) {
            type = "break"
            kind = "keynote"
        }

        if (Pattern.compile(".*(plénière|annonce|^fondations|new comers|newcomers|keynote).*").matcher(title.toLowerCase()).matches()) {
            type = "keynote"
            kind = "keynote"
        }

        if (summary != null) {
            if (summary.contains("#rex", true)) type = "REX"
            if (summary.contains("#hands-on", true)) type = "Hands'On"

            if (summary.contains("#devops", true)) track = "DevOps"
            if (summary.contains("#front", true)) track = "Front"
            if (summary.contains("#agile", true)) track = "Agile"
            if (summary.contains("#back", true)) track = "Back"
            if (summary.contains("#data", true)) track = "Data"
            if (summary.contains("#data", true)) track = "Data"
            if (summary.contains("#mobile", true)) track = "Mobile"
            if (summary.contains("#cloud", true)) track = "Cloud"
            if (summary.contains("#iot", true)) track = "IoT"
            if (summary.contains("#craft", true)) track = "Craft"
            // DataXDay 2019
            if (summary.contains("#dataIntimacy", true)) track = "DataIntimacy"
            if (summary.contains("#reactiveFirst", true)) track = "ReactiveFirst"
            if (summary.contains("#mlDoneRight", true)) track = "MLDoneRight"
        }
    }
}
