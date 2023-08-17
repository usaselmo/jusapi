package authentication.api

import core.api.model.*

interface UserServices {

    fun signup(userRegistrationRequest: UserRegistrationRequest): core.api.model.User?
    fun signup(oAuthUserRegistrationRequest: OAuthUserRegistrationRequest): core.api.model.User?
    fun increaseBalance(userId: core.api.model.UserId, credit: Credit)
    fun delete(userId: core.api.model.UserId)
    fun block(userId: core.api.model.UserId)
    fun deleteAccount(userId: core.api.model.UserId)
    fun blockAccount(userId: core.api.model.UserId)
    fun find(userId: core.api.model.UserId): core.api.model.User?
    fun exists(userId: core.api.model.UserId): Boolean

}

data class UserRegistrationRequest(
    val name: Name,
    val email: Email,
    val password: Password,
    val initialCredit: Credit
)

data class OAuthUserRegistrationRequest(
    val name: Name,
    val email: Email,
    val userId: core.api.model.UserId,
    val password: Password
)
