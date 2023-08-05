package authentication.api

import model.Email
import model.Password
import model.UserId

interface Authenticator {
    fun authenticate(userId: UserId, password: Password): Authentication
    fun authenticate(email: Email, password: Password): Authentication
}

data class Authentication(
    val isAuthenticated: Boolean,
    val errorMessages: List<String>,
) {

    companion object {
        fun failed(errorMessage: String) =
            Authentication(
                isAuthenticated = false,
                errorMessages = listOf(errorMessage)
            )

        fun succeded() =
            Authentication(
                isAuthenticated = true,
                errorMessages = listOf()
            )
    }
}
