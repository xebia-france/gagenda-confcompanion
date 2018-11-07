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
            Pattern.compile(".*(handson|codelab|hands'on|hands on).*").matcher(title.toLowerCase()).matches() -> "Hands'On"
            Pattern.compile(".*(workshop).*").matcher(title.toLowerCase()).matches() -> "Workshop"
            else -> "Talk"
        }

        if (Pattern.compile(".*(closing|evening party|lunch|afternoon break|morning break|breakfast|plénière|tisanes|annonce|^fondations|new comers|déjeuner|newcomers|after xke$|^welcoming|^opening|keynote|^break$|^lunch|^cocktail|^pause).*").matcher(title.toLowerCase()).matches()) {
            type = "keynote"
            kind = "keynote"
        }

        if (title.toLowerCase().indexOf("pitch") == 0 || title.toLowerCase().indexOf("pitch") == title.toLowerCase().length - "pitch".length) {
            type = "keynote"
            kind = "keynote"
        }

        if (summary != null) {
            when {
                summary.contains("#rex", true) -> type = "REX"
                summary.contains("#hands-on", true) -> type = "Hands'On"

                summary.contains("#devops", true) -> track = "DevOps"
                summary.contains("#front", true) -> track = "Front"
                summary.contains("#agile", true) -> track = "Agile"
                summary.contains("#back", true) -> track = "Back"
                summary.contains("#data", true) -> track = "Data"
                summary.contains("#mobile", true) -> track = "Mobile"
                summary.contains("#cloud", true) -> track = "Cloud"
                summary.contains("#iot", true) -> track = "IoT"
                summary.contains("#craft", true) -> track = "Craft"
            }
        }
    }
}
