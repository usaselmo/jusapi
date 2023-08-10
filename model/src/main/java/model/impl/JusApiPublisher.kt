package model.impl

import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class JusApiPublisher<in T : DomainEvent, in S : Subscriber<T>> : Publisher<T, S> {

    private val subscribers2 = linkedMapOf<String, MutableSet<Consumer<T>>>()
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