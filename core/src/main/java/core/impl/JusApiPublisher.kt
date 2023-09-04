package core.impl

import core.api.event.DomainEvent
import core.api.event.Publisher
import core.api.event.Subscriber
import javax.inject.Named

@Suppress("UNCHECKED_CAST")
@Named
class JusApiPublisher<T : DomainEvent, S : Subscriber<T>> : Publisher {

    private val subscribers = linkedMapOf<String, MutableSet<S>>()

    override fun <T2 : DomainEvent> publish(event: T2) {
        synchronized(this) {
            subscribers.forEach { (key, subscribers) ->
                if (key == event.javaClass.name)
                    subscribers.forEach { it.handle(event as T) }
            }
        }
    }

    override fun <T2 : DomainEvent, S2 : Subscriber<T2>> subscribe(key: Class<T2>, subscriber: S2) {
        synchronized(this) {
            subscribers[key.name]?.add(subscriber as S) ?: run {
                subscribers[key.name] = linkedSetOf(subscriber as S)
            }
        }
    }
}