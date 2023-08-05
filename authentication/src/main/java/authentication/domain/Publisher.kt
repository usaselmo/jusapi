package authentication.domain

interface Publisher {
    fun publish(event: DomainEvent)
    fun publish(event: ApplicationEvent)
}