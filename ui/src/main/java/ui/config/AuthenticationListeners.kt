package ui.config

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
import org.springframework.stereotype.Component

@Configuration
class AuthenticationListeners {

    @Bean
    fun authenticationEventPublisher(applicationEventPublisher: ApplicationEventPublisher?): AuthenticationEventPublisher {
        log.info("Authentication Event Publisher created...")
        return DefaultAuthenticationEventPublisher(applicationEventPublisher)
    }

}

val log: Log = LogFactory.getLog(AuthenticationListeners::class.java)

@Component
class AuthenticationEvents {
    init {
        log.info("AuthenticationEvents created...")
    }
    @EventListener
    fun onSuccess(success: AuthenticationSuccessEvent?) {
        log.info("Authentication success event handled")
    }

    @EventListener
    fun onFailure(failures: AbstractAuthenticationFailureEvent?) {
        log.info("Authentication failure event handled")
    }
}



