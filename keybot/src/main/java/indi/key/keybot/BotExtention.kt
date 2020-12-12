package indi.key.keybot

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.toExternalImage
import java.io.File
import kotlin.math.min

suspend fun File.uploadImageSafely(master: Contact, messageEvent: MessageEvent): Image {
    val externalImage = toExternalImage()
    master.sendMessage(master.uploadImage(externalImage))
    return messageEvent.subject.uploadImage(externalImage)
}

suspend fun Contact.sendMessageSafely(environment: Environment, message: Message) {
    val lastSendTime = environment.lastSendTime ?: 0
    val abandonInterval = environment.abandonInterval ?: 0.2
    val dangerInterval = 3.0
    val currentTime = System.currentTimeMillis()

    if (currentTime < lastSendTime + abandonInterval) {
        // Too frequency!!
        return
    }
    sendMessage(message) // enable to send
    if (currentTime < lastSendTime + abandonInterval + dangerInterval) {
        // A bit dangerous. Double the threshold
        environment.abandonInterval = abandonInterval * 2
    } else {
        // Safe! Clear the threshold
        environment.abandonInterval = null
    }
    environment.lastSendTime = currentTime
}