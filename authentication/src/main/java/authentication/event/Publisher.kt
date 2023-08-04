package authentication.event

import authentication.domain.ApplicationEvent
import authentication.domain.DomainEvent
import authentication.domain.EventPublisher
import org.springframework.stereotype.Component

@Component
class Publisher : EventPublisher {
    override fun publish(event: DomainEvent) {
        println("publishing an domain event ....")
        // TODO("Not yet implemented")
    }

    override fun publish(event: ApplicationEvent) {
        println("publishing an application event ....")
        // TODO("Not yet implemented")
    }
}