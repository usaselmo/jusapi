package authentication.api

import core.api.model.*

interface UserServices {

    fun signup(userRegistrationInput: UserRegistrationInput)
    fun signup(oAuthUserRegistrationInput: OAuthUserRegistrationInput)
    fun increaseBalance(userId: UserId, credit: Credit)
    fun delete(userId: UserId)
    fun block(userId: UserId)
    fun deleteAccount(userId: UserId)
    fun blockAccount(userId: UserId)
    fun find(userId: UserId): User?
    fun exists(userId: UserId): Boolean

}

data class UserRegistrationInput(
    val name: Name,
    val email: Email,
    val password: Password,
    val initialCredit: Credit
)

data class OAuthUserRegistrationInput(
    val name: Name,
    val email: Email,
    val userId: UserId,
    val password: Password
)
