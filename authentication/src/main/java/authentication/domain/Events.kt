package authentication.domain

import authentication.api.Authentication
import model.Access
import model.User
import model.UserId

data class UserCreatedDomainEvent(val user: User) : DomainEvent

data class UserAccessRegisteredDomainEvent(val user: User, val access: Access) : DomainEvent

data class UserAuthenticatedDomainEvent(val userId: UserId): DomainEvent

interface ApplicationEvent

interface DomainEvent