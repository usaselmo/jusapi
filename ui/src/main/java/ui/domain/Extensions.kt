package ui.domain

import core.api.model.*
import core.api.model.Role.USER
import org.springframework.security.oauth2.core.user.OAuth2User
import java.time.LocalDateTime
import java.util.*

fun OAuth2User.toModel() =
    User(
        id = UserId((attributes["id"] as Int).toString()),
        name = Name(attributes["name"] as String),
        email = Email(attributes["email"] as String),
        createdAt = LocalDateTime.now(),
        account = Account(
            id = AccountId(UUID.randomUUID().toString()),
            type = AccountType.STANDARD,
            createdAt = LocalDateTime.now(),
            usage = Usage(0L, 1000L),
            isBlocked = false,
            isDeleted = false
        ),
        isDeleted = false,
        mutableSetOf(USER)
    ) //TODO