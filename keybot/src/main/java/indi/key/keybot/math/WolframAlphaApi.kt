package indi.key.keybot.math

import indi.key.keybot.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

object WolframAlphaApi {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    suspend fun downloadImage(urlString: String): File {
        return withContext(Dispatchers.IO) {
            val file = File("walfram_alpha_image", "${urlString.hashCode()}.gif")
            println("download image: $urlString -> $file")
            if (file.exists()) {
                return@withContext file
            }
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val request: Request = Request.Builder()
                .url(urlString)
                .get()
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body ?: throw IOException("no body!")
                    body.byteStream().use { inputStream ->
                        file.writeBytes(inputStream.readBytes())
                    }
                }
                file
            } catch (exception: Throwable) {
                exception.printStackTrace()
                throw exception
            }
        }
    }

    suspend fun process(input: String): Document {
        val url = "http://api.wolframalpha.com/v2/query".toHttpUrl()
            .newBuilder()
            .addQueryParameter("input", input)
            .addQueryParameter("appid", Environment.userInfo.wolframAlphaAppId)
            .build()
        println("walfram alpha url: $url")
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body ?: throw IOException("no body!")
                    body.byteStream().use { inputStream ->
                        DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(inputStream)
                    }
                }
            } catch (exception: Throwable) {
                exception.printStackTrace()
                throw exception
            }
        }
    }
}

fun NodeList.toIterable(): Iterable<Node> {
    return object : AbstractList<Node>() {
        override val size: Int
            get() = length

        override fun get(index: Int): Node = item(index)
    }
}