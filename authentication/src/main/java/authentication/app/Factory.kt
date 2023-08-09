package authentication.app

import model.api.*
import model.api.event.Publisher
import model.api.event.UserCreatedDomainEvent
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function

class Factory(
    private val publisher: Publisher,
) {

    val intFunction: (Int) -> Int = { i -> i }
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
        }.also {
            publisher.publish(UserCreatedDomainEvent(it))
        }


}

enum class InitialAction {
    SET_INITIAL_CREDIT_ACCORDING_TO_ACCOUNT_TYPE,
    SET_ZERO_INITIAL_CREDIT
}
