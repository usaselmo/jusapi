package authentication.domain.repository

import model.Email
import model.Password
import model.User
import model.UserId

interface UserRepository {
    fun find(email: Email, password: Password): User
    fun find(userId: UserId, password: Password): User
}
