package authentication.domain.repository

import model.Email
import model.Password
import model.User

interface UserRepository {
    fun find(email: Email, password: Password): User

}
