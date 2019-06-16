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
        // TODO extract this in appropriate type
        val HANDS_ON_PATTERN = ".*(hands-on|handson|codelab|hands'on|hands on).*"
        val HANDS_ON_LABEL = "hands-on"
        val WORKSHOP_PATTERN = ".*(workshop).*"
        val WORKSHOP_LABEL = "workshop"
        val TALK_LABEL = "talk"
        val KEYNOTE_LABEL = "keynote"
        val KEYNOTE_KIND = "keynote"
        val CLOSING_PATTERN = ".*(^closing|clôture$).*"
        val CLOSING_LABEL = "closing"
        val PARTY_PATTERN = ".*(evening party|after xke$|^cocktail|tisanes).*"
        val PARTY_LABEL = "party"
        val LUNCH_PATTERN = ".*(lunch|déjeuner).*"
        val LUNCH_LABEL = "lunch"
        val OPENING_PATTERN = ".*(^welcoming|^opening|opening$|^accueil|mot des organisateurs).*"
        val OPENING_LABEL = "opening"
        val BREAKFAST_PATTERN = ".*(breakfast).*"
        val BREAKFAST_LABEL = "breakfast"
        val BREAK_PATTERN = ".*(meet and greet|afternoon break|morning break|^break$|^pause|^encas).*"
        val BREAK_LABEL = "break"
        val KEYNOTE_PATTERN = ".*(plénière|annonce|^fondations|new comers|newcomers|keynote).*"

        val GENERAL_KEYNOTE_PATTERN = listOf(CLOSING_PATTERN, PARTY_PATTERN, LUNCH_PATTERN, OPENING_PATTERN, BREAKFAST_PATTERN, BREAK_PATTERN, KEYNOTE_PATTERN).joinToString("|")

        val lowerCaseTitle = title.toLowerCase()

        type = when {
            matchesPattern(HANDS_ON_PATTERN, lowerCaseTitle) -> HANDS_ON_LABEL
            matchesPattern(WORKSHOP_PATTERN, lowerCaseTitle) -> WORKSHOP_LABEL
            matchesPattern(CLOSING_PATTERN, lowerCaseTitle) -> CLOSING_LABEL
            matchesPattern(PARTY_PATTERN, lowerCaseTitle) -> PARTY_LABEL
            matchesPattern(LUNCH_PATTERN, lowerCaseTitle) -> LUNCH_LABEL
            matchesPattern(OPENING_PATTERN, lowerCaseTitle) -> OPENING_LABEL
            matchesPattern(BREAKFAST_PATTERN, lowerCaseTitle) -> BREAKFAST_LABEL
            matchesPattern(BREAK_PATTERN, lowerCaseTitle) -> BREAK_LABEL
            matchesPattern(KEYNOTE_PATTERN, lowerCaseTitle) -> KEYNOTE_LABEL
            else -> TALK_LABEL
        }

        if (matchesPattern(GENERAL_KEYNOTE_PATTERN, lowerCaseTitle)) {
            kind = KEYNOTE_KIND
        }

        if (summary != null) {
            if (summary.contains("#rex", true)) type = "REX"
            if (summary.contains("#hands-on", true)) type = "Hands'On"
            if (summary.contains("#workshop", true)) type = "Workshop"

            if (summary.contains("#devops", true)) track = "DevOps"
            if (summary.contains("#front", true)) track = "Front"
            if (summary.contains("#agile", true)) track = "Agile"
            if (summary.contains("#back", true)) track = "Back"
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

    private fun matchesPattern(pattern: String, string: String) =
            Pattern.compile(pattern).matcher(string).matches()
}
