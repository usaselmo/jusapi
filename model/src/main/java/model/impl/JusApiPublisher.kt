package model.impl

import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Component
class JusApiPublisher<in T : DomainEvent, in S : Subscriber<T>> : Publisher<T, S> {
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
            log.info("Registering subscriber: ${key.name}")
        }
    }

    companion object {
        val log: Log = LogFactory.getLog(this::class.java)
    }
}