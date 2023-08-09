package ui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(value = ["ui", "authentication", "trabalhista", "model"])
class Main


fun main(args: Array<String>) {
    runApplication<Main>(*args)

    val publisher = MainPublisher<DomainEvent, Subscriber<DomainEvent>>()

    val accessSubscriber = AccessSubscriber()
    val authenticationSubscriber = AuthenticationSubscriber()

    publisher.subscribe(UserAccessRegisteredDomainEvent::class.java, accessSubscriber as Subscriber<DomainEvent>)
    publisher.subscribe(UserAuthenticatedDomainEvent::class.java, authenticationSubscriber as Subscriber<DomainEvent>)

    publisher.publish(UserAccessRegisteredDomainEvent("Test ............. "))
}


interface DomainEvent
interface Subscriber<in T : DomainEvent> {
    fun handle(event: T)
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

data class UserAuthenticatedDomainEvent(
    val name: String
) : DomainEvent {
    init {
        println("publishing event: $name")
    }
}

data class UserAccessRegisteredDomainEvent(
    val name: String
) : DomainEvent {
    init {
        println("publishing event: $name")
    }
}

interface Publisher<in T : DomainEvent, in S : Subscriber<T>> {
    fun publish(event: T)
    fun <T> subscribe(key: Class<T>, subscriber: S)
}

class MainPublisher<in T : DomainEvent, in S : Subscriber<T>> : Publisher<T, S> {

    private val subscribers = linkedMapOf<String, MutableSet<S>>()
    override fun publish(event: T) {
        subscribers.forEach { (k, v) ->
            if (k == event.javaClass.name)
                v.forEach { it.handle(event) }
        }
    }

    override fun <T> subscribe(key: Class<T>, subscriber: S) {
        subscribers[key.name]?.add(subscriber) ?: run {
            subscribers[key.name] = linkedSetOf(subscriber)
        }.also {
            println("Registering subscriber: ${key.name}")
        }
    }
}