package authentication.impl.repository

import authentication.domain.repository.UserRepository
import model.Email
import model.Password
import model.User
import model.UserId
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