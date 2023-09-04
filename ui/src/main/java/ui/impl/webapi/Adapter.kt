package ui.impl.webapi

import authentication.api.UserServices
import core.api.model.User
import core.api.model.UserId
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import ui.domain.UIException

@Component
class Adapter(
    private val userServices: UserServices
) {

    fun toModel(oAuth2User: OAuth2User): User =
        userServices.find(userId = UserId((oAuth2User.attributes["id"] as Int).toString()))
            ?: throw UIException("Usuário não encontrado")

}
