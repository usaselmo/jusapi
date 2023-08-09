package authentication.impl.event

import model.api.event.DomainEvent
import model.api.event.EventSubscriber
import model.api.event.Publisher
import org.springframework.stereotype.Component

@Component
class JusApiEventsSubscriber(
    private val publisher: Publisher
) : EventSubscriber {
    override fun handle(event: DomainEvent) {
        TODO("Not yet implemented")
    }

}