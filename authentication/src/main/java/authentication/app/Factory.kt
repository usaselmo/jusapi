package authentication.app

import authentication.api.Email
import authentication.api.UserId
import authentication.domain.EventPublisher
import authentication.domain.model.*
import authentication.domain.vo.Name
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
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
