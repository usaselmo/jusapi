package authentication.domain.repository

import model.api.Email
import model.api.Password
import model.api.User
import model.api.UserId

interface UserRepository {
    fun find(email: Email, password: Password): User
    fun find(userId: UserId, password: Password): User
    fun find(userId: UserId): User?
    fun save(user: User)
}
