package core.impl

import core.api.event.DomainEvent
import core.api.event.Publisher
import core.api.event.Subscriber
import org.springframework.stereotype.Component

@Suppress("UNCHECKED_CAST")
@Component
class JusApiPublisher<T : DomainEvent, S : Subscriber<T>> : Publisher {

    private val subscribers = linkedMapOf<String, MutableSet<S>>()

    override fun <T2 : DomainEvent> publish(event: T2) {
        try {
            synchronized(this) {
                subscribers.forEach { (key, subscribers) ->
                    if (key == event.javaClass.name)
                        subscribers.forEach { it.handle(event as T) }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun <T3 : DomainEvent, S3 : Subscriber<T3>> subscribe(key: Class<T3>, subscriber: S3) {
        try {
            synchronized(this) {
                subscribers[key.name]?.add(subscriber as S) ?: run {
                    subscribers[key.name] = linkedSetOf(subscriber as S)
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}