package authentication.domain.repository

import core.api.model.Email
import core.api.model.Password
import core.api.model.User
import core.api.model.UserId

interface UserRepository {
    fun find(email: Email, password: Password): User?
    fun find(userId: UserId, password: Password): User?
    fun find(userId: UserId): User?
    fun update(user: User)
    fun signup(user: User, password: Password)
}
