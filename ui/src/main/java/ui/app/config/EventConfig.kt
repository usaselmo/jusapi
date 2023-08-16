package ui.app.config

import model.api.event.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component
import ui.app.config.EventConfig.Companion.log

@Suppress("UNCHECKED_CAST")
@Component
class EventConfig(
    publisher: Publisher
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

    companion object {
        val log: Log = LogFactory.getLog(EventConfig::class.java)
    }

}


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
