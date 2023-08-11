package authentication.api

import model.api.Credit
import model.api.Email
import model.api.Name
import model.api.Password

data class UserRegistrationRequest (
    val name: Name,
    val email: Email,
    val password: Password,
    val initialCredit: Credit
)
