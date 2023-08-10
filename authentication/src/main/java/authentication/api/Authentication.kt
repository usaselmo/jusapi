package authentication.api

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