package authentication.impl

import authentication.api.Authentication
import authentication.api.Authentication.Companion.failed
import authentication.api.Authenticator
import authentication.domain.Messages.USUARIO_CONTA_BLOQUEADA
import authentication.domain.Messages.USUARIO_CONTA_DELETADA
import authentication.domain.Messages.USUARIO_DELETADO
import authentication.domain.Messages.USUARIO_NAO_TEM_CREDITOS
import authentication.domain.repository.UserRepository
import model.api.*
import model.api.event.*
import org.springframework.stereotype.Component

@Component
class ApplicationAuthenticator(
    private val publisher: Publisher<DomainEvent, Subscriber<DomainEvent>>,
    private val userRepository: UserRepository,
) : Authenticator {

    fun registerUserAccess(user: User, access: Access): User {
        return user.registerAccess().also {
            publisher.publish(
                UserAccessRegisteredDomainEvent(user = user, access = access)
            )
        }
    }

    override fun authenticate(userId: UserId, password: Password): Authentication =
        checkAuthentication(userRepository.find(userId, password))
            .also {
                if (it.isAuthenticated)
                    publisher.publish(UserAuthenticatedDomainEvent(userId = userId))
            }

    override fun authenticate(email: Email, password: Password): Authentication {
        userRepository.find(email, password).let { user ->
            return checkAuthentication(user)
        }
    }

    private fun checkAuthentication(user: User): Authentication {
        return when {
            user.hasNoBalance() -> failed(USUARIO_NAO_TEM_CREDITOS)
            user.isDeleted -> failed(USUARIO_DELETADO)
            user.accountIsDeleted() -> failed(USUARIO_CONTA_DELETADA)
            user.accountIsBlocked() -> failed(USUARIO_CONTA_BLOQUEADA)
            else -> Authentication.succeeded()
        }
    }

}