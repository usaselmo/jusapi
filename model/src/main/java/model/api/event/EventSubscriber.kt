package model.api.event


interface Subscriber<in T : DomainEvent> {
    fun handle(event: T)
}