import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import com.squareup.moshi.Types
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import java.io.File
import java.io.InputStream

data class XebiaWhoIsSpeaker(var file: String, var name: String, var imageUrl: String)
data class XebiaBlogSpeaker(var firstName: String, var lastName: String, var bio: String)

object SpeakerFetcher {
    lateinit var localUsers: List<XebiaWhoIsSpeaker>

    init {
        loadLocalData()
    }

    fun fetch(userId: String): Speaker {
        val speaker = Speaker(userId)

        val xebiaBlogSpeaker = fetchDataFromBlog(userId)
        val localSpeaker = fetchDataFromLocal(userId)

        speaker.bio = xebiaBlogSpeaker.bio

        if (xebiaBlogSpeaker.firstName.isNotEmpty()) {
            speaker.firstName = xebiaBlogSpeaker.firstName
            speaker.lastName = xebiaBlogSpeaker.lastName
        }

        localSpeaker?.let {
            if (speaker.firstName.isNullOrEmpty()) {
                speaker.firstName = localSpeaker.name
            }
            if (speaker.imageURL.isNullOrEmpty()) {
                speaker.imageURL = localSpeaker.imageUrl
            }
        }

        if (speaker.firstName.isNullOrEmpty()) {
            speaker.firstName = speaker.id
        }

        if (speaker.imageURL == "") {
            speaker.imageURL = "https://www.gravatar.com/avatar/${DigestUtils.md5Hex(speaker.id.toLowerCase().trim() + "@xebia.fr")}?s=400"
        }

        return speaker
    }

    private fun fetchDataFromBlog(userId: String): XebiaBlogSpeaker {
        val blogSpeaker = XebiaBlogSpeaker("", "", "")

        try {
            val xebiaUrl = "https://blog.xebia.fr/author/$userId"
            val (_, _, result) = xebiaUrl.httpGet().responseString()

            when (result) {
                is Result.Success -> {
                    val data = result.get()

                    val doc = Jsoup.parse(data)

                    doc.head().getElementsByAttributeValueStarting("property", "profile:")
                            .forEach {
                                when (it.attr("property")) {
                                    "profile:first_name" -> {
                                        blogSpeaker.firstName = it.attr("content")
                                    }
                                    "profile:last_name" -> {
                                        blogSpeaker.lastName = it.attr("content")
                                    }
                                }
                            }

                    doc.body().getElementsByClass("description")
                            .firstOrNull()?.let {
                        blogSpeaker.bio = it.html()
                    }
                }
            }
            return blogSpeaker
        } catch (e: Exception) {
            return blogSpeaker
        }
    }

    private fun fetchDataFromLocal(userId: String): XebiaWhoIsSpeaker? {
        var potentialUser: XebiaWhoIsSpeaker? = null

        for (i in 0..minOf(4, userId.length)) {
            potentialUser = localUsers
                    .firstOrNull { xebiaUser ->
                        var a = userId.substring(i).toLowerCase()
                        a = StringUtils.stripAccents(a)

                        var b = xebiaUser.name.replace(" ", "").toLowerCase()
                        b = StringUtils.stripAccents(b)

                        a != "" && b.endsWith(a)
                    }
            if (potentialUser != null) break
        }

        return if (potentialUser != null) {
            potentialUser.imageUrl = "http://whois.xebia.fr/static/xebians/${potentialUser.file}"
            potentialUser
        } else {
            println("$userId is unknown")
            null
        }
    }

    private fun loadLocalData() {
        val inputStream: InputStream = File("src/main/resources/users.json").inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }
        val speakerType = Types.newParameterizedType(List::class.java, XebiaWhoIsSpeaker::class.java)

        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        val gson = gsonBuilder.create()
        localUsers = gson.fromJson(inputString, speakerType)
    }
}