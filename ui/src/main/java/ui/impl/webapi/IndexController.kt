package ui.impl.webapi

import core.api.email.Body
import core.api.email.EmailSender
import core.api.email.To
import core.api.model.Email
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ui.domain.UIException
import ui.impl.adapter.Adapter

@RestController
class IndexController(
    private val emailSender: EmailSender,
    private val adapter: Adapter
) {

    val log: Log = LogFactory.getLog(javaClass)

    @GetMapping(value = ["", "/"])
    fun index(@AuthenticationPrincipal principal: OAuth2User): String {
        val user = adapter.toModel(principal)
        emailSender.send(
            to = To(destinations = setOf(Email("anselmo.sr@gmail.com"))),
            body = Body("")
        )
        return "Sucesso!!!"
    }

}
