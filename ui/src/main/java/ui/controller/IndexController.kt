package ui.controller

import authentication.api.Authenticator
import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ui.adapter.Adapter

@RestController
class IndexController(
    private val publisher: Publisher<DomainEvent, Subscriber<DomainEvent>>,
    private val authenticator: Authenticator,
    private val adapter: Adapter
) {

    val log: Log = LogFactory.getLog(javaClass)

    @GetMapping(value = ["", "/"])
    fun index(@AuthenticationPrincipal principal: OAuth2User): String {
        val user = adapter.toModel(principal)
        return "Sucesso!!!"
    }

}