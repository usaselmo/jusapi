package authentication.app

import authentication.domain.DomainEvent
import authentication.domain.model.User
import authentication.domain.vo.Access

class Event

data class UserCreatedDomainEvent(val user: User) : DomainEvent

data class UserAccessRegisteredDomainEvent(val user: User, val access: Access) : DomainEvent


