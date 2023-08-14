package ui.config

import authentication.api.OAuthUserRegistrationRequest
import authentication.api.UserServices
import model.api.Email
import model.api.Name
import model.api.Password
import model.api.UserId
import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import model.api.event.UserAuthenticatedDomainEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import ui.Messages.ERRO_AO_REGISTRAR_NOVO_USUARIO
import ui.exception.UIException

@Configuration
class AuthenticationListeners(
    private val userServices: UserServices
) {

    @Bean
    fun authenticationEventPublisher(applicationEventPublisher: ApplicationEventPublisher?): AuthenticationEventPublisher {
        log.info("Authentication Event Publisher created...")
        return DefaultAuthenticationEventPublisher(applicationEventPublisher)
    }

}

val log: Log = LogFactory.getLog(AuthenticationListeners::class.java)

@Component
class AuthenticationEvents(
    private val userServices: UserServices,
    private val publisher: Publisher<DomainEvent, Subscriber<DomainEvent>>
) {
    init {
        log.info("AuthenticationEvents created...")
    }

    @EventListener
    fun onSuccess(success: AuthenticationSuccessEvent) {
        try {
            (success.authentication.principal as OAuth2User).let { oAuth2User ->
                UserId((oAuth2User.attributes["id"] as Int).toString()).let { userId ->
                    if (!userServices.exists(userId)) {
                        userServices.register(
                            OAuthUserRegistrationRequest(
                                name = Name(
                                    fullName = oAuth2User.attributes["name"] as String,
                                    loginName = oAuth2User.attributes["login"] as String
                                ),
                                email = Email(oAuth2User.attributes["email"] as String),
                                password = Password.empty(),
                                userId = UserId((oAuth2User.attributes["id"] as Int).toString())
                            )
                        )
                    }
                    publisher.publish(UserAuthenticatedDomainEvent(userId))
                }
            }
        } catch (e: Exception) {
            log.error(e.message)
            throw UIException(ERRO_AO_REGISTRAR_NOVO_USUARIO)
        }
    }

    @EventListener
    fun onFailure(failures: AbstractAuthenticationFailureEvent?) {
        log.info("Authentication failure event handled")
    }
}



