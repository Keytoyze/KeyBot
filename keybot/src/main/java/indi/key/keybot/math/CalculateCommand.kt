package indi.key.keybot.math

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.util.sendAndRecord
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText


object CalculateCommand : BaseCommand() {
    override val command = "计算"
    override val help = "超强计算器，帮你计算、化简、求解、求导、积分！"
    override val postFixHelp = "[表达式/函数/方程/不等式]"
    override val willUseEnvironment = false

    private val titleMap = mapOf(
        "Result" to "结果",
        "Real solution" to "实数解",
        "Alternate form" to "等价形式",
        "Solution" to "解",
        "Root" to "根",
        "Complex root" to "复数根",
        "Real root" to "实数根",
        "Implicit derivative" to "隐微分",
        "Partial derivative" to "偏导数",
        "Numerical solution" to "数值解",
        "Numerical root" to "数值根",
        "Derivative" to "导数",
        "Indefinite integral" to "不定积分",
        "Integer root" to "整数根",
        "Global minimum" to "最小值",
        "Global maximum" to "最大值",
        "Power of 10 representation" to "用10的幂表达",
        "Number length" to "数字长度",
        "Last few decimal digit" to "最后几位"
    )

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        val result = StringBuilder("输入：$arguments\n===================")
        var hasResult = false
        val timeStart = System.currentTimeMillis()
        val document = WolframAlphaApi.process(arguments)
        val timeUsed = System.currentTimeMillis() - timeStart
        document.getElementsByTagName("pod").toIterable().forEach { pod ->
            var title = pod.attributes.getNamedItem("title").textContent
            if (title.endsWith('s')) {
                title = title.substring(0, title.length - 1)
            }
            println("title: $title -> ${titleMap[title]}")
            titleMap[title]?.let { titleTranslate ->
                val items = pod.childNodes.toIterable()
                    .map { subPod ->
                        subPod.childNodes.toIterable()
                            .firstOrNull { subpodElement -> subpodElement.nodeName == "plaintext" }
                            ?.textContent
                            ?.replace("element", "∈")
                            ?.replace("log", "ln")
                            ?.replace("integral", "∫")
                            ?.replace("constant", "C")
                            ?.replace("decimal digits", "位")
                            ?.replace("(no roots exist)", "无解")
                    }
                    .filterNotNull()
                    .filterNot { it.isBlank() }
                if (items.isNotEmpty()) {
                    hasResult = true
                    result.append("\n\uD83D\uDC49 ").append(titleTranslate).append("：")
                    items.forEach { result.append("\n").append(it) }
                }
            }
        }
        if (!hasResult) {
            result.append("\n诶，这个东西我也不会算 TAT")
        }
        result.append("\n（用时：%.2f 秒）".format(timeUsed.toDouble() / 1000))
        messageEvent.subject.sendAndRecord(PlainText(result))
    }
}