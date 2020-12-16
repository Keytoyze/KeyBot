package indi.key.keybot.math

import indi.key.keybot.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory

object WolframAlphaApi {

    suspend fun process(input: String, subject: Contact, onSuccess: (Document) -> String) {
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
        withContext(Dispatchers.IO) {
            try {
                CalculateCommand.client.newCall(request).execute().use { response ->
                    val body = response.body ?: throw IOException("no body!")
                    body.byteStream().use { inputStream ->
                        val document = DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(inputStream)
                        val result = onSuccess(document)
                        subject.sendMessage(result)
                    }
                }
            } catch (exception: Throwable) {
                exception.printStackTrace()
                subject.sendMessage("请求错误！原因：${exception.message}")
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