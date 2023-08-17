package core.api.event

import core.api.model.Access
import core.api.model.User
import core.api.model.UserId

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
