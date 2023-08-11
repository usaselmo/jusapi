package authentication.api

import model.api.*

interface UserServices {

    fun register(userRegistrationRequest: UserRegistrationRequest): User?
    fun increaseBalance(userId: UserId, credit: Credit)
    fun delete(userId: UserId)
    fun block(userId: UserId)
    fun deleteAccount(userId: UserId)
    fun blockAccount(userId: UserId)

}