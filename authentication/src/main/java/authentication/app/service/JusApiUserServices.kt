package authentication.app.service


import authentication.api.UserRegistrationRequest
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
import model.api.Credit
import model.api.User
import model.api.UserId
import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import model.api.event.UserCreatedDomainEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Component
class JusApiUserServices(
    private val userRepository: UserRepository,
    private val publisher: Publisher<DomainEvent, Subscriber<DomainEvent>>,
    private val factory: Factory
) : UserServices {
    override fun register(userRegistrationRequest: UserRegistrationRequest): User? {
        try {
            return factory.newUser(
                name = userRegistrationRequest.name.loginName,
                email = userRegistrationRequest.email.value
            ) {
                it.zeroCredits()
                    .increaseBalance(userRegistrationRequest.initialCredit)
            }.let { userCreated ->
                userRepository.save(userCreated)
                publisher.publish(UserCreatedDomainEvent(userCreated.id))
                userCreated
            }
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException("$ERROR_AO_REGISTRAR_NOVO_USUARIO: $userRegistrationRequest.name")
        }
    }

    override fun increaseBalance(userId: UserId, credit: Credit) {
        try {
            userRepository.find(userId)?.increaseBalance(credit)?.let {
                userRepository.save(it)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_AUMENTAR_CREDITO_DE_USUARIO)
        }
    }

    override fun delete(userId: UserId) {
        try {
            userRepository.find(userId)?.delete()?.let {
                userRepository.save(it)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_DELETAR_USUARIO)
        }
    }

    override fun block(userId: UserId) {
        try {
            userRepository.find(userId)?.blockAccount()?.let {
                userRepository.save(it)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException("")
        }
    }

    override fun deleteAccount(userId: UserId) {
        try {
            userRepository.find(userId)?.deleteAccount()?.let { userAccountDeleted ->
                userRepository.save(userAccountDeleted)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_DELETAR_CONTA_DE_USUARIO)
        }
    }

    override fun blockAccount(userId: UserId) {
        try {
            userRepository.find(userId)?.blockAccount()?.let { userAccountBlocked ->
                userRepository.save(userAccountBlocked)
            } ?: throw AuthenticationException(USUARIO_NAO_ENCONTRADO)
        } catch (e: Exception) {
            log.error(e.message)
            throw AuthenticationException(ERROR_AO_BLOQUEAR_CONTA_DE_USUARIO)
        }
    }

    companion object {
        val log: Log = LogFactory.getLog(this::class.java)
    }
}