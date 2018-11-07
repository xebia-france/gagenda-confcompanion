import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStreamReader
import java.util.*

private val LOG = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val spreadsheetId = System.getenv("SPREADSHEET")
    val range = System.getenv("RANGE")
    val schedule = System.getenv("SCHEDULE")
    val storeDir = System.getenv("S3_DIR")

    val app = SpreadsheetToSpeakers(spreadsheetId, range, schedule, storeDir)

    app.generateSpeakers()
}

class SpreadsheetToSpeakers(
        private val spreadsheetId: String,
        private val range: String,
        private val scheduleUrl: String,
        private val storeDir: String) {

    companion object {
        const val CREDENTIAL_FOLDER = "credentials"
        const val CLIENT_SECRET = "client_secret.json"
        const val ACCESS_TYPE_OFFLINE = "offline"
        const val AUTH_ID = "user"
        val APP_NAME: String = SpreadsheetToSpeakers::class.java.simpleName
        val BUILD_DIR: String = "${File("").absolutePath}/build"
    }

    private val transport = GoogleNetHttpTransport.newTrustedTransport()
    private val factory = JacksonFactory.getDefaultInstance()
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()
    private val scheduleType = Types.newParameterizedType(List::class.java, Talk::class.java)
    private val scheduleAdapter = moshi.adapter<List<Talk>>(scheduleType)
    private val speakersType = Types.newParameterizedType(List::class.java, Speaker::class.java)
    private val speakersAdapter = moshi.adapter<List<Speaker>>(speakersType)
    private val store = AwsS3Store()

    private var schedule: List<Talk>? = null

    fun generateSpeakers() {
        val spreadsheet = getContent()
        if (spreadsheet == null || spreadsheet.isEmpty()) {
            LOG.warn { "No data found" }
        } else {
            downloadSchedule()
            val speakers = mutableListOf<Speaker>()
            val sLines = dropFirstLines(spreadsheet)
            sLines.forEach { line ->
                val speaker = buildSpeaker(line)
                if (speaker.isValid()) {
                    speakers.add(speaker)
                }
            }
            LOG.debug { speakers }
            File("$BUILD_DIR/speakers.json").printWriter().use { out ->
                out.print(speakersAdapter.toJson(speakers))
            }
            LOG.info { "Speakers generated in $BUILD_DIR/speakers.json" }
            store.putSpeakers(storeDir, "$BUILD_DIR/speakers.json")
            LOG.info { "Speakers sent to bucket $storeDir" }
            schedule = generateSchedule(speakers)
            LOG.debug { schedule }
            File("$BUILD_DIR/schedule.json").printWriter().use { out ->
                out.print(scheduleAdapter.toJson(schedule))
            }
            LOG.info { "Schedule generated in $BUILD_DIR/schedule.json" }
            store.putSchedule(storeDir, "$BUILD_DIR/schedule.json")
            LOG.info { "Schedule sent to bucket $storeDir" }
        }
    }

    private fun generateSchedule(speakers: List<Speaker>): List<Talk>? =
            schedule?.let { schedule ->
                schedule.forEach { talk ->
                    speakers.forEach { speaker ->
                        val speakerInTalk = speaker.talks.find { t -> t.id == talk.id } != null
                        if (speakerInTalk) {
                            speaker.id?.let {
                                talk.speakers.add(TalkSpeaker(it, "${speaker.firstName} ${speaker.lastName}"))
                            }
                        }
                    }
                }
                schedule
            }

    private fun downloadSchedule() {
        val request = Request.Builder().url(scheduleUrl).build()
        val body = client.newCall(request).execute().body()
        body?.let {
            schedule = scheduleAdapter.fromJson(it.string())
            LOG.debug { schedule }
        }
    }

    private fun buildSpeaker(line: List<Any>): Speaker {
        val speaker = Speaker()
        line.forEachIndexed { index, column ->
            val value = column as String
            when (index) {
                0 -> speaker.firstName = value
                1 -> speaker.lastName = value
                2 -> speaker.bio = value
                3 -> speaker.tweetHandle = value
                4 -> speaker.imageURL = value
                5, 6 -> {
                    val talk = buildTalk(value)
                    if (talk != null) {
                        speaker.talks.add(talk)
                    }
                }
            }
        }
        speaker.generateId()
        return speaker
    }

    private fun buildTalk(talkId: String): SpeakerTalk? {
        return schedule?.let {
            val talk = it.find { t -> t.id == talkId }
            if (talk != null) {
                return SpeakerTalk(talk.id, talk.title)
            }
            return null
        }
    }

    private fun dropFirstLines(spreadsheet: List<List<Any>>): List<List<Any>> {
        return spreadsheet.subList(1, spreadsheet.size)
    }

    private fun getContent(): List<List<Any>>? {
        val service = Sheets.Builder(transport, factory, getCredential())
                .setApplicationName(APP_NAME)
                .build()
        val response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
        return response.getValues()
    }

    private fun getCredential(): Credential {
        val s = SpreadsheetToSpeakers::class.java.getResourceAsStream(CLIENT_SECRET)
        val clientSecrets = GoogleClientSecrets.load(factory, InputStreamReader(s))
        val flow = GoogleAuthorizationCodeFlow.Builder(
                transport, factory, clientSecrets, listOf(SheetsScopes.SPREADSHEETS_READONLY))
                .setDataStoreFactory(FileDataStoreFactory(File(CREDENTIAL_FOLDER)))
                .setAccessType(ACCESS_TYPE_OFFLINE)
                .build()
        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize(AUTH_ID)
    }
}

data class TalkSpeaker(val id: String, val name: String)

data class Talk(
        val conferenceId: String,
        val id: String,
        val fromTime: String,
        val kind: String,
        val speakers: MutableList<TalkSpeaker>,
        val title: String,
        val toTime: String,
        val type: String,
        val room: String,
        val track: String,
        val summary: String)

data class SpeakerTalk(val id: String, val title: String)

class Speaker {
    var firstName: String? = null
    var lastName: String? = null
    var bio: String? = null
    var tweetHandle: String? = null
    var imageURL: String? = null
    var talks: MutableList<SpeakerTalk> = mutableListOf()
    var id: String? = null

    fun isValid(): Boolean = id !== null

    override fun toString(): String {
        return "Speaker(firstName=$firstName, lastName=$lastName, bio=$bio, tweetHandle=$tweetHandle, imageURL=$imageURL, talks=$talks)"
    }

    fun generateId() {
        firstName?.let { fn ->
            if (fn.isNotEmpty()) {
                lastName?.let { ln ->
                    if (ln.isNotEmpty()) {
                        val alphabetic = Regex("[^A-Za-z0-9]")
                        id = alphabetic.replace("${fn[0].toLowerCase()}${ln.toLowerCase()}${UUID.randomUUID()}", "")
                    }
                }
            }
        }
    }
}

class AwsS3Store {
    companion object {
        const val CLIENT_SECRET = "aws_secret.json"
        const val CONTENT_TYPE_JSON = "application/json"
        const val ENCODE_UTF8 = "UTF-8"
    }

    private var s3: AmazonS3? = null
    private var config: AWSConfigurationFile? = null

    init {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(AWSConfigurationFile::class.java)

        config = moshi.adapter<AWSConfigurationFile>(type)
                .fromJson(AwsS3Store::class.java.getResource(CLIENT_SECRET).readText())

        config?.apply {
            val credentials = BasicAWSCredentials(access_key, secret_key)
            s3 = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(AWSStaticCredentialsProvider(credentials))
                    .build()
        }
    }

    private fun putObject(filename: String, filePath: String) {
        val file = File(filePath)

        val md = ObjectMetadata()
        md.contentLength = file.length()
        md.contentType = CONTENT_TYPE_JSON
        md.contentEncoding = ENCODE_UTF8

        config?.run {
            val putObjectRequest = PutObjectRequest(bucketName, filename, file.inputStream(), md)
            s3?.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead))
        }
    }

    fun putSchedule(dir: String, src: String) {
        putObject("$dir/schedule.json", src)
    }

    fun putSpeakers(dir: String, src: String) {
        putObject("$dir/speakers.json", src)
    }
}

data class AWSConfigurationFile(val access_key: String, val secret_key: String, val region: String, val bucketName: String)

