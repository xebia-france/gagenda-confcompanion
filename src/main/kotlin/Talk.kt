import java.util.*
import java.util.regex.Pattern

data class Talk(val conferenceId: String, val fromTime: Date, val id: String, val toTime: Date, var title: String, val speakers: List<SpeakerTalk>? = null, val summary: String?, var room: String?) {

    var type: String = ""
    var kind: String? = null

    init {
        if (Pattern.compile(".*(handson|codelab|hands'on).*").matcher(title.toLowerCase()).matches()) {
            type = "Hands'On"
        } else if (Pattern.compile(".*(workshop).*").matcher(title.toLowerCase()).matches()) {
            type = "Workshop"
        } else {
            type = "Talk"
        }

        if (Pattern.compile(".*(plénière|tisanes|annonce|pitch|^fondations|new comers|déjeuner).*").matcher(title.toLowerCase()).matches()) {
            type = "keynote"
            kind = "keynote"
        }
    }
}