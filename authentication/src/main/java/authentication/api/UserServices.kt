package authentication.api

import model.api.*
import model.api.Credit
import model.api.Email
import model.api.Name
import model.api.Password

interface UserServices {

    fun register(userRegistrationRequest: UserRegistrationRequest): User?
    fun increaseBalance(userId: UserId, credit: Credit)
    fun delete(userId: UserId)
    fun block(userId: UserId)
    fun deleteAccount(userId: UserId)
    fun blockAccount(userId: UserId)

}

data class UserRegistrationRequest(
    val name: Name,
    val email: Email,
    val password: Password,
    val initialCredit: Credit
)
