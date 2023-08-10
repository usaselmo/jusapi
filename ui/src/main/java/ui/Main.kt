package ui

import authentication.app.Factory
import model.api.Access
import model.api.event.DomainEvent
import model.api.event.Subscriber
import model.api.event.UserAccessRegisteredDomainEvent
import model.api.event.UserAuthenticatedDomainEvent
import model.impl.JusApiPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(value = ["ui", "authentication", "trabalhista", "model"])
class Main : CommandLineRunner {

    @Autowired
    lateinit var jusApiPublisher: JusApiPublisher<DomainEvent, Subscriber<DomainEvent>>

    @Autowired lateinit var factory: Factory

    override fun run(vararg args: String?) {

        val accessSubscriber = AccessSubscriber()
        val authenticationSubscriber = AuthenticationSubscriber()

        jusApiPublisher.subscribe(UserAccessRegisteredDomainEvent::class.java, accessSubscriber as Subscriber<DomainEvent>)
        jusApiPublisher.subscribe(
            UserAuthenticatedDomainEvent::class.java,
            authenticationSubscriber as Subscriber<DomainEvent>
        )

        jusApiPublisher.publish(UserAccessRegisteredDomainEvent(factory.newUser("nome", "email@gmail.com"), Access()))
        jusApiPublisher.publish(UserAuthenticatedDomainEvent(
            factory.newUser("nome", "email@gmail.com").id
        ))
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}


class AccessSubscriber : Subscriber<UserAccessRegisteredDomainEvent> {
    override fun handle(event: UserAccessRegisteredDomainEvent) {
        println("handle event: $event")
    }
}

class AuthenticationSubscriber : Subscriber<UserAuthenticatedDomainEvent> {
    override fun handle(event: UserAuthenticatedDomainEvent) {
        println("handle event: $event")
    }
}

