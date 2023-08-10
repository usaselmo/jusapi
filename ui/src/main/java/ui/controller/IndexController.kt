package ui.controller

import model.api.Access
import model.api.event.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ui.toModel

@RestController
class IndexController(
    private val publisher: Publisher<DomainEvent, Subscriber<DomainEvent>>
) {

    val log: Log = LogFactory.getLog(javaClass)

    @GetMapping(value = ["", "/"])
    fun index(@AuthenticationPrincipal principal: OAuth2User): String {
        val user = principal.toModel()
        publisher.publish(UserAuthenticatedDomainEvent(user.id))
        publisher.publish(UserAccessRegisteredDomainEvent(user, Access()))
        return "Sucesso!!!"
    }

}