package authentication.app.service


import authentication.api.OAuthUserRegistrationInput
import authentication.api.UserRegistrationInput
import authentication.api.UserServices
import authentication.app.Factory
import authentication.domain.AuthenticationException
import authentication.domain.Messages.ERROR_AO_AUMENTAR_CREDITO_DE_USUARIO
import authentication.domain.Messages.ERROR_AO_BLOQUEAR_CONTA_DE_USUARIO
import authentication.domain.Messages.ERROR_AO_DELETAR_CONTA_DE_USUARIO
import authentication.domain.Messages.ERROR_AO_DELETAR_USUARIO
import authentication.domain.Messages.ERROR_AO_REGISTRAR_NOVO_USUARIO
import authentication.domain.Messages.USUARIO_NAO_ENCONTRADO
import authentication.domain.repository.UserRepository
import core.api.event.Publisher
import core.api.event.UserCreatedDomainEvent
import core.api.model.Credit
import core.api.model.Role
import core.api.model.Role.FINANCIAL_USER
import core.api.model.User
import core.api.model.UserId
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Component
class JusApiUserServices(
    private val userRepository: UserRepository,
    private val publisher: Publisher,
    private val factory: Factory
) : UserServices {

    override fun signup(oAuthUserRegistrationInput: OAuthUserRegistrationInput) =
        try {
            factory.newStandardUser(
                oAuthUserRegistrationInput.userId,
                oAuthUserRegistrationInput.email,
                oAuthUserRegistrationInput.name
            ).let { newUser ->
                userRepository.signup(newUser, oAuthUserRegistrationInput.password)
                log.info("new user signed up")
                publisher.publish(UserCreatedDomainEvent(newUser.id))
            }
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException("$ERROR_AO_REGISTRAR_NOVO_USUARIO: $oAuthUserRegistrationInput.name")
        }

    override fun signup(userRegistrationInput: UserRegistrationInput) {
        try {
            val newStandardUser = factory.newStandardUser(userRegistrationInput.name.loginName, userRegistrationInput.email.value)
            userRepository.signup(newStandardUser, userRegistrationInput.password)
            publisher.publish(UserCreatedDomainEvent(newStandardUser.id))
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException("$ERROR_AO_REGISTRAR_NOVO_USUARIO: $userRegistrationInput.name")
        }
    }

    override fun increaseBalance(userId: UserId, credit: Credit) {
        try {
            userRepository.find(userId)?.increaseBalance(credit)?.let {
                userRepository.update(it)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_AUMENTAR_CREDITO_DE_USUARIO)
        }
    }

    override fun delete(userId: UserId) {
        try {
            userRepository.find(userId)?.delete()?.let {
                userRepository.update(it)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_DELETAR_USUARIO)
        }
    }

    override fun block(userId: UserId) {
        try {
            userRepository.find(userId)?.blockAccount()?.let {
                userRepository.update(it)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException("")
        }
    }

    override fun deleteAccount(userId: UserId) {
        try {
            userRepository.find(userId)?.deleteAccount()?.let { userAccountDeleted ->
                userRepository.update(userAccountDeleted)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_DELETAR_CONTA_DE_USUARIO)
        }
    }

    override fun blockAccount(userId: UserId) {
        try {
            userRepository.find(userId)?.blockAccount()?.let { userAccountBlocked ->
                userRepository.update(userAccountBlocked)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_BLOQUEAR_CONTA_DE_USUARIO)
        }
    }

    override fun find(userId: UserId): User? =
        userRepository.find(userId)

    override fun exists(userId: UserId): Boolean =
        userRepository.find(userId)?.let { true } ?: false

    companion object {
        val log: Log = LogFactory.getLog(this::class.java)
    }
}