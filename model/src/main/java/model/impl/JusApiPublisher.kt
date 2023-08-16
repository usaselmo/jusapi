package model.impl

import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Suppress("UNCHECKED_CAST")
@Component
class JusApiPublisher<T : DomainEvent, S : Subscriber<T>> : Publisher {

    private val subscribers = linkedMapOf<String, MutableSet<S>>()

    override fun <T2 : DomainEvent> publish(event: T2) {
        subscribers.forEach { (k, v) ->
            if (k == event.javaClass.name)
                v.forEach { it.handle(event as T) }
        }
    }

    override fun <T2 : DomainEvent, S2 : Subscriber<T2>> subscribe(key: Class<T2>, subscriber: S2) {
        subscribers[key.name]?.add(subscriber as S) ?: run {
            subscribers[key.name] = linkedSetOf(subscriber as S)
        }.also {
            log.info("Registering subscriber: ${key.name}")
        }
    }

    companion object {
        val log: Log = LogFactory.getLog(this::class.java)
    }
}