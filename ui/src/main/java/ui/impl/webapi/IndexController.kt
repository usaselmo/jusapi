package ui.impl.webapi

import authentication.api.Authenticator
import core.api.event.Publisher
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
    private val publisher: Publisher,
    private val authenticator: Authenticator,
    private val adapter: Adapter
) {

    val log: Log = LogFactory.getLog(javaClass)

    @GetMapping(value = ["", "/"])
    fun index(@AuthenticationPrincipal principal: OAuth2User): String {
        val user = adapter.toModel(principal)
        if(true)
            throw UIException("erro de proposito")
        return "Sucesso!!!"
    }

}
