import java.util.*
import java.util.regex.Pattern

data class Talk(val fromTime: Date, val id: String, val toTime: Date, val title: String) {

    private var type: String = ""

    init {
        if (Pattern.compile(".*(handson|codelab).*").matcher(title.toLowerCase()).matches()) {
            type = "Hands'On"
        } else if (Pattern.compile(".*(workshop).*").matcher(title.toLowerCase()).matches()) {
            type = "Workshop"
        } else {
            type = "Talk"
        }
    }

}