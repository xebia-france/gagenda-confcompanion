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


    val HANDS_ON_PATTERN = ".*(hands-on|handson|codelab|hands'on|hands on).*"
    val WORKSHOP_PATTERN = ".*(workshop).*"
    val KEYNOTE_PATTERN = ".*(closing|evening party|lunch|afternoon break|morning break|breakfast|plénière|tisanes|annonce|^fondations|new comers|déjeuner|newcomers|after xke$|^welcoming|^opening|keynote|^break$|^lunch|^cocktail|^pause|^pitch|pitch$).*"
    val HANDS_ON_LABEL = "Hands-on"
    val WORKSHOP_LABEL = "Workshop"
    val TALK_LABEL = "Talk"
    val KEYNOTE_LABEL = "keynote"
    val KEYNOTE_KIND = "keynote"

    var type: String = ""
    var kind: String? = null
    var track: String? = null

    init {
        val lowerCaseTitle = title.toLowerCase()

        type = when {
            matchesPattern(HANDS_ON_PATTERN, lowerCaseTitle) -> HANDS_ON_LABEL
            matchesPattern(WORKSHOP_PATTERN, lowerCaseTitle) -> WORKSHOP_LABEL
            else -> TALK_LABEL
        }

        if (matchesPattern(KEYNOTE_PATTERN, lowerCaseTitle)) {
            type = KEYNOTE_LABEL
            kind = KEYNOTE_KIND
        }

        if (summary != null) {
            if (summary.contains("#rex", true)) type = "REX"
            if (summary.contains("#hands-on", true)) type = "Hands'On"

            if (summary.contains("#devops", true)) track = "DevOps"
            if (summary.contains("#front", true)) track = "Front"
            if (summary.contains("#agile", true)) track = "Agile"
            if (summary.contains("#back", true)) track = "Back"
            if (summary.contains("#data", true)) track = "Data"
            if (summary.contains("#mobile", true)) track = "Mobile"
            if (summary.contains("#cloud", true)) track = "Cloud"
            if (summary.contains("#iot", true)) track = "IoT"
            if (summary.contains("#craft", true)) track = "Craft"
        }
    }

    private fun matchesPattern(pattern: String, string: String) =
            Pattern.compile(pattern).matcher(string).matches()
}
