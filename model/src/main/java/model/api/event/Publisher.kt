package model.api.event


interface Publisher {
    fun <T : DomainEvent> publish(event: T)
    fun <T : DomainEvent, S : Subscriber<T>> subscribe(key: Class<T>, subscriber: S)
}
