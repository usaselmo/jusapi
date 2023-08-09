package model.api.event

interface EventSubscriber {
    fun handle(event: DomainEvent)
}