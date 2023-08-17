package authentication.api

import core.api.model.Email
import core.api.model.Password
import core.api.model.UserId

interface Authenticator {
    fun authenticate(userId: UserId, password: Password): Authentication?
    fun authenticate(email: Email, password: Password): Authentication?
}

data class Authentication(
    val isAuthenticated: Boolean,
    val errorMessages: List<String>,
) {

    companion object {
        fun failed(vararg errorMessage: String) =
            Authentication(
                isAuthenticated = false,
                errorMessages = listOf(*errorMessage)
            )

        fun succeeded() =
            Authentication(
                isAuthenticated = true,
                errorMessages = listOf()
            )
    }
}
