package ui.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController {

    @GetMapping(value = ["","/"])
    fun index(@AuthenticationPrincipal principal: OAuth2User): String {

        println("" + Jackson2ObjectMapperBuilder.json().build<ObjectMapper?>().writeValueAsString(principal) )
        return "Sucesso!!!"
    }

}