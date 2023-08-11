package authentication.api

import model.api.Email
import model.api.Password
import model.api.UserId

interface Authenticator {
    fun authenticate(userId: UserId, password: Password): Authentication?
    fun authenticate(email: Email, password: Password): Authentication?
}

