package authentication.impl.repository

import authentication.domain.AuthenticationException
import authentication.domain.repository.UserRepository
import model.api.Email
import model.api.Password
import model.api.User
import model.api.UserId
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Component
class AuthenticationUserRepository : UserRepository {

    private val users = mutableListOf<UserTable>();

    override fun find(email: Email, password: Password): User? =
        users.find { it.user.email == email && it.password == password }?.user

    override fun find(userId: UserId, password: Password): User? =
        users.find { it.user.id == userId && it.password == password }?.user

    override fun find(userId: UserId): User? =
        users.find { it.user.id == userId }?.user

    override fun update(user: User) {
        users
            .filter { it.user.id == user.id }
            .first { userTable ->
                users.remove(userTable)
                users.add(
                    UserTable(
                        password = userTable.password,
                        user = user
                    )
                )
            }
    }

    override fun register(user: User, password: Password) {
        if (users.any { it.user.id == user.id || it.user.email == user.email }) {
            log.error("Element exists already: $user")
            throw AuthenticationException("Element exists already")
        }
        users.add(
            UserTable(
                password = password,
                user = user
            )
        )
    }

    companion object {
        val log: Log = LogFactory.getLog(this::class.java)
    }
}

data class UserTable(
    val password: Password,
    val user: User
)




