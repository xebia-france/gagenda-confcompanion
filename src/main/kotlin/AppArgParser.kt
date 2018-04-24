import com.xenomachina.argparser.ArgParser

class AppArgParser(parser: ArgParser) {
    val rooms by parser.flagging("enable room computation")
    val calendar by parser.storing("calendar id (Google) containing conference's agenda")
    val from by parser.storing("day of beginning format: $DATE_FORMAT")
    val duration by parser.storing("duration in day")
}