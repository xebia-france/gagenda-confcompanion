import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.Okio
import java.io.File

class AWSS3Persister {
    private var s3Client: AmazonS3
    private var awsConfigurationFile: AWSConfigurationFile

    init {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(AWSConfigurationFile::class.java)

        awsConfigurationFile = moshi.adapter<AWSConfigurationFile>(type).fromJson(Okio.buffer(Okio.source(File("./credentials/gagenda-confcompanion/aws_secret.json")))) ?: throw ExceptionInInitializerError("AWS configuration file is missing")

        val credentials = BasicAWSCredentials(awsConfigurationFile.access_key, awsConfigurationFile.secret_key)
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(awsConfigurationFile.region)
                .withCredentials(AWSStaticCredentialsProvider(credentials))
                .build()
    }

    private val acl = AccessControlList().apply {
        grantPermission(GroupGrantee.AllUsers, Permission.Read)
    }


    private fun putObject(remotePath: String, objectPath: String) {
        val file = File(objectPath)
        val md = ObjectMetadata()
        md.contentLength = file.length()
        md.contentType = "application/json"
        md.contentEncoding = "UTF-8"

        s3Client.putObject(
                PutObjectRequest(awsConfigurationFile.bucketName, remotePath, File(objectPath).inputStream(), md)
                        .withAccessControlList(acl))

    }

    fun putSchedule(objectPath: String) {
        putObject(awsConfigurationFile.scheduleFilePath, objectPath)
    }

    fun putSpeakers(objectPath: String) {
        putObject(awsConfigurationFile.speakersFilePath, objectPath)
    }

}

data class AWSConfigurationFile(val access_key: String, val secret_key: String, val region: String, val bucketName: String, val scheduleFilePath: String, val speakersFilePath: String)
