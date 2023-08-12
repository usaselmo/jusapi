package authentication.app

import model.api.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function

@Component
class Factory {
    fun newUser(name: String, email: String, function: Function<User, User>? = Function { it }): User =
        with(
            User(
                id = UserId(value = UUID.randomUUID().toString()),
                name = Name(name),
                email = Email(email),
                createdAt = LocalDateTime.now(),
                account = Account(
                    id = AccountId(value = UUID.randomUUID().toString()),
                    type = AccountType.STANDARD,
                    createdAt = LocalDateTime.now(),
                    usage = Usage(
                        count = 0L,
                        credit = 0L
                    ),
                    isBlocked = false,
                    isDeleted = false
                ),
                isDeleted = false
            )
        ) {
            function?.apply(this) ?: this
        }
}
