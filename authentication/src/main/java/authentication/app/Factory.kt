package authentication.app

import authentication.domain.EventPublisher
import authentication.domain.model.*
import authentication.domain.vo.Name
import model.Email
import model.UserId
import java.time.LocalDateTime
import java.util.*

class Factory(
    private val eventPublisher: EventPublisher,
) {

    fun newUser(name: String, email: String) =
        User(
            id = UserId(value = UUID.randomUUID().toString()),
            name = Name(name),
            email = Email(email),
            createdAt = LocalDateTime.now(),
            account = Account(
                id = AccountId(value = UUID.randomUUID().toString()),
                type = AccountType.STANDARD,
                createdAt = LocalDateTime.now(),
                usage = Usage(count = 0L),
                isBlocked = false,
                isDeleted = false
            ),
            isDeleted = false
        ).also { user ->
            eventPublisher.publish(UserCreatedDomainEvent(user))
        }
}
