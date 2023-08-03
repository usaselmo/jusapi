package authentication.domain

interface DomainEvent

interface ApplicationEvent

interface EventPublisher {
    fun publish(event: DomainEvent)
    fun publish(event: ApplicationEvent)
}
