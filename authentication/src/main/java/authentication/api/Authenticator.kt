package authentication.api

import model.Email


interface Authenticator {
    fun authenticate(email: Email)
}