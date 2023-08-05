package ui.controller

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ui.toModel

@RestController
class IndexController {

    val log: Log = LogFactory.getLog(javaClass)

    @GetMapping(value = ["", "/"])
    fun index(@AuthenticationPrincipal principal: OAuth2User): String {
        log.info(principal.toModel().id)
        return "Sucesso!!!"
    }

}