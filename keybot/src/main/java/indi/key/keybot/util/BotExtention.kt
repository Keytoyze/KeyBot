package indi.key.keybot.util

import indi.key.keybot.Environment
import indi.key.keybot.learn.ChatRecorder
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.asMessageChain
import net.mamoe.mirai.utils.toExternalImage
import java.io.File

suspend fun File.uploadImageSafely(master: Contact, messageEvent: MessageEvent): Image {
    val externalImage = toExternalImage()
    master.sendMessage(master.uploadImage(externalImage))
    return messageEvent.subject.uploadImage(externalImage)
}

suspend fun Contact.sendAndRecord(message: Message) {
    sendMessage(message) // enable to send
    ChatRecorder.save(message.asMessageChain(), this, Environment.userInfo.qq)
}

suspend fun Contact.sendMessageSafely(environment: Environment, message: Message) {
    val lastSendTime = environment.lastSendTime ?: 0
    val abandonInterval = environment.abandonInterval ?: 200
    val dangerInterval = 3000
    val currentTime = System.currentTimeMillis()

    if (currentTime < lastSendTime + abandonInterval) {
        // Too frequency!!
        return
    }
    sendAndRecord(message)
    if (currentTime < lastSendTime + abandonInterval + dangerInterval) {
        // A bit dangerous. Double the threshold
        environment.abandonInterval = abandonInterval * 2
    } else {
        // Safe! Clear the threshold
        environment.abandonInterval = null
    }
    environment.lastSendTime = currentTime
}

suspend fun Contact.sendMessageSafely(environment: Environment, message: String) {
    sendMessageSafely(environment, PlainText(message))
}