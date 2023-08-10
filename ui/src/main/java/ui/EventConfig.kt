package ui

import model.api.event.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Suppress("UNCHECKED_CAST")
@Component
class EventConfig(
    publisher: Publisher<DomainEvent, Subscriber<DomainEvent>>
) {
    init {
        publisher.subscribe(
            UserAccessRegisteredDomainEvent::class.java,
            AccessSubscriber() as Subscriber<DomainEvent>
        )
        publisher.subscribe(
            UserAuthenticatedDomainEvent::class.java,
            AuthenticationSubscriber() as Subscriber<DomainEvent>
        )
    }
}


val log: Log = LogFactory.getLog(EventConfig::class.java)

class AccessSubscriber : Subscriber<UserAccessRegisteredDomainEvent> {
    override fun handle(event: UserAccessRegisteredDomainEvent) {
        log.info("handle event: $event")
    }
}

class AuthenticationSubscriber : Subscriber<UserAuthenticatedDomainEvent> {
    override fun handle(event: UserAuthenticatedDomainEvent) {
        log.info("handle event: $event")
    }
}
