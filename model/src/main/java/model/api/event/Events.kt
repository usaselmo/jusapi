package model.api.event

import model.api.Access
import model.api.User
import model.api.UserId
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

//Events

interface DomainEvent
interface ApplicationEvent


val log: Log = LogFactory.getLog(ApplicationEvent::class.java)

data class UserAuthenticatedDomainEvent(
    val userId: UserId
) : DomainEvent {
    init {
        log.info("publishing user authenticated event.  user id: ${userId.value}")
    }
}

data class UserAccessRegisteredDomainEvent(
    val user: User,
    val access: Access
) : DomainEvent {
    init {
        log.info("publishing user access registered event: ${user.name}")
    }
}

data class UserCreatedDomainEvent(
    val userId: UserId
) : DomainEvent {
    init {
        log.info("publishing user created event: $userId")
    }
}
