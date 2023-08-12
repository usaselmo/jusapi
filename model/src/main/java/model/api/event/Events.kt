package model.api.event

import model.api.Access
import model.api.User
import model.api.UserId

interface DomainEvent
interface ApplicationEvent

data class UserAuthenticatedDomainEvent(
    val userId: UserId
) : DomainEvent

data class UserAccessRegisteredDomainEvent(
    val user: User,
    val access: Access
) : DomainEvent

data class UserCreatedDomainEvent(
    val userId: UserId
) : DomainEvent
