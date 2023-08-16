package authentication.impl

import authentication.api.Authentication
import authentication.api.Authentication.Companion.failed
import authentication.api.Authenticator
import authentication.domain.AuthenticationException
import authentication.domain.Messages.ERROR_AO_AUTENTICAR_USUARIO
import authentication.domain.Messages.USUARIO_CONTA_BLOQUEADA
import authentication.domain.Messages.USUARIO_CONTA_DELETADA
import authentication.domain.Messages.USUARIO_DELETADO
import authentication.domain.Messages.USUARIO_NAO_ENCONTRADO
import authentication.domain.Messages.USUARIO_NAO_TEM_CREDITOS
import authentication.domain.repository.UserRepository
import model.api.*
import model.api.event.Publisher
import model.api.event.UserAccessRegisteredDomainEvent
import model.api.event.UserAuthenticatedDomainEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Component
class ApplicationAuthenticator(
    private val publisher: Publisher,
    private val userRepository: UserRepository,
) : Authenticator {

    fun registerUserAccess(user: User, access: Access): User {
        try {
            return user.registerAccess().also {
                publisher.publish(
                    UserAccessRegisteredDomainEvent(user = user, access = access)
                )
            }
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_AUTENTICAR_USUARIO)
        }
    }

    override fun authenticate(userId: UserId, password: Password): Authentication =
        try {
            userRepository.find(userId, password)?.let {
                checkAuthentication(it)
                    .also { authentication ->
                        if (authentication.isAuthenticated)
                            publisher.publish(UserAuthenticatedDomainEvent(userId = userId))
                    }
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_AUTENTICAR_USUARIO)
        }


    override fun authenticate(email: Email, password: Password): Authentication {
        try {
            userRepository.find(email, password)?.let { user ->
                return checkAuthentication(user)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_AUTENTICAR_USUARIO)
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

    companion object {
        val log: Log = LogFactory.getLog(this::class.java)
    }


}