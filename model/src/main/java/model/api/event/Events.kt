package model.api.event

import model.api.Access
import model.api.User
import model.api.UserId

//Events

interface DomainEvent
interface ApplicationEvent

data class UserAuthenticatedDomainEvent(
    val userId: UserId
) : DomainEvent {
    init {
        println("publishing user authenticated event.  user id: ${userId.value}")
    }
}

data class UserAccessRegisteredDomainEvent(
    val user: User,
    val access: Access
) : DomainEvent {
    init {
        println("publishing user access registered event: ${user.name}")
    }
}

data class UserCreatedDomainEvent(
    val user: User
) : DomainEvent {
    init {
        println("publishing user created event: ${user.name}")
    }
}