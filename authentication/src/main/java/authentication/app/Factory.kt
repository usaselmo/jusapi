package authentication.app

import authentication.domain.Publisher
import authentication.domain.UserCreatedDomainEvent
import model.*
import java.time.LocalDateTime
import java.util.*

class Factory(
    private val publisher: Publisher,
) {

    fun newUser(name: String, email: String, vararg initialAction: InitialAction): User =
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
        ).let {
            when {
                initialAction.contains(InitialAction.SET_INITIAL_CREDIT_ACCORDING_TO_ACCOUNT_TYPE) -> it.setInitialCredit()
                initialAction.contains(InitialAction.SET_ZERO_INITIAL_CREDIT) -> it.zerarCreditos()
                else -> it
            }
        }.also {
            publisher.publish(UserCreatedDomainEvent(it))
        }


}

enum class InitialAction {
    SET_INITIAL_CREDIT_ACCORDING_TO_ACCOUNT_TYPE,
    SET_ZERO_INITIAL_CREDIT
}
