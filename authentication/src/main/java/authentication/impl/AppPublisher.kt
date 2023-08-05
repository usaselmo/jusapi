package authentication.impl

import authentication.domain.ApplicationEvent
import authentication.domain.DomainEvent
import authentication.domain.Publisher
import org.springframework.stereotype.Component

@Component
class AppPublisher : Publisher {
    override fun publish(event: DomainEvent) {
        println("publishing an domain event ....")
        // TODO("Not yet implemented")
    }

    override fun publish(event: ApplicationEvent) {
        println("publishing an application event ....")
        // TODO("Not yet implemented")
    }
}