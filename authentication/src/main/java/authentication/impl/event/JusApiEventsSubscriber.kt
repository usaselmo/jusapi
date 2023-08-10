package authentication.impl.event

import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import org.springframework.stereotype.Component

@Component
class JusApiEventsSubscriber(
    private val publisher: Publisher<DomainEvent, Subscriber<DomainEvent>>
) : Subscriber<DomainEvent> {
    override fun handle(event: DomainEvent) {
        TODO("Not yet implemented")
    }

}