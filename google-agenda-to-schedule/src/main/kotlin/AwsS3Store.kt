import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File

class AwsS3Store {
    companion object {
        const val CLIENT_SECRET = "credentials/aws_secret.json"
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
