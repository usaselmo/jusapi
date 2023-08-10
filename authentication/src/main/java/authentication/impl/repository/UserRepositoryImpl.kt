package authentication.impl.repository

import authentication.app.Factory
import authentication.domain.repository.UserRepository
import model.api.Email
import model.api.Password
import model.api.User
import model.api.UserId
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val factory: Factory //tirar isso depois
) : UserRepository {
    override fun find(email: Email, password: Password): User {
        return factory.newUser("nome", "email@gmail.com")
    }

    override fun find(userId: UserId, password: Password): User {
        return factory.newUser("nome", "email@gmail.com")
    }
}