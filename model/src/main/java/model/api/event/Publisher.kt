package model.api.event

interface Publisher {

    fun subscribe(eventName: String, listener: EventSubscriber)
    fun publish(event: DomainEvent)
    fun publish(event: ApplicationEvent)
}