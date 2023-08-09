package authentication.impl.repository

import authentication.domain.repository.UserRepository
import model.api.Email
import model.api.Password
import model.api.User
import model.api.UserId
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl : UserRepository {
    override fun find(email: Email, password: Password): User {
        TODO("Not yet implemented")
    }

    override fun find(userId: UserId, password: Password): User {
        TODO("Not yet implemented")
    }
}