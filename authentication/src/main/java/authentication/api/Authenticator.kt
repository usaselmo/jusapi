package authentication.api

import model.api.Email
import model.api.Password
import model.api.UserId

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

        fun succeeded() =
            Authentication(
                isAuthenticated = true,
                errorMessages = listOf()
            )
    }
}
