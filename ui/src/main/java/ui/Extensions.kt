package ui

import model.Email
import model.Name
import model.User
import org.springframework.security.oauth2.core.user.OAuth2User

fun OAuth2User.toModel() =
    User(
        name = Name(this.attributes["name"] as String),
        email = Email(this.attributes["email"] as String)
    )