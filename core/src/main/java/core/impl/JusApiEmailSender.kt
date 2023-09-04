package core.impl

import core.api.email.Attachment
import core.api.email.Body
import core.api.email.EmailSender
import core.api.email.To
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Named

@Named
class JusApiEmailSender : EmailSender {

    private val service: ExecutorService = Executors.newFixedThreadPool(8)

    override fun send(to: To, body: Body, vararg attachment: Attachment) {
        service.submit {
            println("Sending email to $to in thread ${Thread.currentThread().name}")
            //TODO implementar isso
        }
    }
}