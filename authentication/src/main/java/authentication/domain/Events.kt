package authentication.domain

import model.Access
import model.User

data class UserCreatedDomainEvent(val user: User) : DomainEvent

data class UserAccessRegisteredDomainEvent(val user: User, val access: Access) : DomainEvent

interface ApplicationEvent

interface DomainEvent