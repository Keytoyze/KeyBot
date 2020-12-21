package indi.key.keybot.math

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.util.sendAndRecord
import indi.key.keybot.util.uploadImageSafely
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder

object DrawCommand : BaseCommand() {
    override val command = "画图"
    override val help = "绘制图像和曲线。支持2D和3D图。"
    override val postFixHelp = "[函数/方程/不等式]"
    override val showHelp = true
    override val willUseEnvironment = false

    val titleMap = mapOf(
        "plot" to "图像",
        "3d plot" to "三维图像",
        "contour plot" to "等高线",
        "surface plot" to "曲面图",
        "inequality plot" to "不等式图像"
    )

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        val resultBuilder = MessageChainBuilder().append("输入：$arguments\n===================")
        var hasResult = false
        val timeStart = System.currentTimeMillis()
        val document = WolframAlphaApi.process(arguments)
        document.getElementsByTagName("pod").toIterable().forEach { pod ->
            var title = pod.attributes.getNamedItem("title").textContent.toLowerCase()
            if (title.endsWith('s')) {
                title = title.substring(0, title.length - 1)
            }
            println("title: $title -> ${titleMap[title]}")
            titleMap[title]?.let { titleTranslate ->
                val images = pod.childNodes.toIterable()
                    .map { subPod ->
                        subPod.childNodes.toIterable()
                            .firstOrNull { subpodElement -> subpodElement.nodeName == "img" }
                            ?.attributes
                            ?.getNamedItem("src")
                            ?.textContent
                    }
                    .filterNotNull()
                    .filterNot { it.isBlank() }
                    .map { url ->
                        WolframAlphaApi.downloadImage(url).uploadImageSafely(master, messageEvent)
                    }
                if (!images.isNullOrEmpty()) {
                    hasResult = true
                    resultBuilder.append("\n\uD83D\uDC49 ")
                        .append(titleTranslate)
                        .append("：")
                    images.forEach { image -> resultBuilder.append(image) }
                }
            }
        }
        val timeUsed = System.currentTimeMillis() - timeStart
        if (!hasResult) {
            resultBuilder.append("\n诶，这个东西我也不会画 TAT")
        }
        resultBuilder.append("\n（用时：%.2f 秒）".format(timeUsed.toDouble() / 1000))
        messageEvent.subject.sendAndRecord(resultBuilder.build())
    }
}