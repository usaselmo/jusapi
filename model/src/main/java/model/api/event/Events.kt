package model.api.event

import model.api.Access
import model.api.User
import model.api.UserId

data class UserCreatedDomainEvent(val user: User) : DomainEvent

data class UserAccessRegisteredDomainEvent(val user: User, val access: Access) : DomainEvent

data class UserAuthenticatedDomainEvent(val userId: UserId): DomainEvent

interface ApplicationEvent

interface DomainEvent