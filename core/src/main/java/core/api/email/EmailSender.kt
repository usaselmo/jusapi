package core.api.email

import core.api.model.Email
import java.io.File

interface EmailSender {
    fun send(to: To, body: Body, vararg attachment: Attachment)
}

data class To(
    val destinations: Set<Email>
)

data class Body(
    val content: String
)

data class Attachment(
    val file: File
)