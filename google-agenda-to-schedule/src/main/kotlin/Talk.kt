import java.util.*
import java.util.regex.Pattern

data class Talk(val conferenceId: String, val fromTime: Date, val id: String, val toTime: Date, var title: String, val speakers: List<SpeakerTalk>? = null, val summary: String?, var room: String?) {

    var type: String = ""
    var kind: String? = null

    init {
        if (Pattern.compile(".*(handson|codelab|hands'on|hands on).*").matcher(title.toLowerCase()).matches()) {
            type = "Hands'On"
        } else if (Pattern.compile(".*(workshop).*").matcher(title.toLowerCase()).matches()) {
            type = "Workshop"
        } else {
            type = "Talk"
        }

        if (Pattern.compile(".*(breakfast|plénière|tisanes|annonce|^fondations|new comers|déjeuner|newcomers|after xke$|^welcoming|^opening|keynote|^break$|^lunch|^cocktail|^pause).*").matcher(title.toLowerCase()).matches()) {
            type = "keynote"
            kind = "keynote"
        }

        if (title.toLowerCase().indexOf("pitch") == 0 || title.toLowerCase().indexOf("pitch") == title.toLowerCase().length - "pitch".length) {
            type = "keynote"
            kind = "keynote"
        }
    }
}