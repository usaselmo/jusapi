package model.api.event



interface Publisher<in T : DomainEvent, in S : Subscriber<T>> {
    fun publish(event: T)
    fun <T> subscribe(key: Class<T>, subscriber: S)
}
