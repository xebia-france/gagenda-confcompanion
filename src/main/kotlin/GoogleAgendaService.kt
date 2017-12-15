import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ArrayMap
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import java.io.File
import java.io.InputStreamReader


class GoogleAgendaService {
    companion object {
        val APP_NAME = "Google Agenda to Conf Companion Converter"
        val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
        val SECRET = "client_secret.json"
        val USER = "user"
        val OFFLINE = "offline"
    }

    private val credentialsDir by lazy {
        File(GoogleAgendaService::class.java.getResource("credentials").path)
    }
    private val jsonFactory by lazy {
        JacksonFactory.getDefaultInstance()
    }
    private val httpTransport by lazy {
        GoogleNetHttpTransport.newTrustedTransport()
    }
    private val dataStoreFactory by lazy {
        FileDataStoreFactory(credentialsDir)
    }

    private fun authorize(): Credential {
        val stream = GoogleAgendaService::class.java.getResourceAsStream("credentials/$SECRET")
        val secrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(stream))

        if (secrets.size < 2) {
            val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets, SCOPES)
                    .setDataStoreFactory(dataStoreFactory)
                    .setAccessType(OFFLINE)
                    .build()

            return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize(USER)
        } else {
            val accessToken = (secrets["auth"] as ArrayMap<String, String>).getValue(0)
            val refreshToken = (secrets["auth"] as ArrayMap<String, String>).getValue(1)

            val credential = GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setClientSecrets(secrets.installed.clientId, secrets.installed.clientSecret).build()

            credential.accessToken = accessToken
            credential.refreshToken = refreshToken

            return credential
        }
    }

    private fun getCalendarService(): Calendar = Calendar.Builder(httpTransport, jsonFactory, authorize())
            .setApplicationName(APP_NAME)
            .build()

    fun getEvents(calendarId: String, from: DateTime, to: DateTime): List<Event> =
            getCalendarService()
                    .events()
                    .list(calendarId)
                    .setTimeMin(from)
                    .setTimeMax(to)
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .execute()
                    .items

}
