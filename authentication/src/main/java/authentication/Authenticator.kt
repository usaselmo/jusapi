package authentication

import model.Email


interface Authenticator {
    fun authenticate(email: Email)
}