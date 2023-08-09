package model.impl

import model.api.event.ApplicationEvent
import model.api.event.DomainEvent
import model.api.event.EventSubscriber
import model.api.event.Publisher
import org.springframework.stereotype.Component

@Component
class JusApiPublisher : Publisher {

    private val eventSubscribers = mutableMapOf<String, EventSubscriber>()
    override fun subscribe(eventName: String, listener: EventSubscriber) {
        eventSubscribers[eventName] = listener
    }

    override fun publish(event: DomainEvent) {
        eventSubscribers.forEach {
            if(it.key == event.javaClass.name)
                it.value.handle(event)
        }
    }

    override fun publish(event: ApplicationEvent) {
        println("publishing an application event: $event")
        // TODO("Not yet implemented")
    }
}