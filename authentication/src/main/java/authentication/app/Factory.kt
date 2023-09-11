package authentication.app

import core.api.model.*
import core.api.model.Role.USER
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function

@Component
class Factory {
    fun newStandardUser(
        name: String,
        email: String,
        function: Function<User, User>? = Function { it }
    ): User {
        val user = User(
            id = UserId(value = UUID.randomUUID().toString()),
            name = Name(name),
            email = Email(email),
            createdAt = LocalDateTime.now(),
            account = newStandardAccount(),
            isDeleted = false,
            mutableSetOf(USER)
        )
        return function?.apply(user) ?: user
    }

    fun newStandardUser(userId: UserId, email: Email, name: Name): User {
        return User(
            id = userId,
            name = name,
            email = email,
            createdAt = LocalDateTime.now(),
            account = newStandardAccount(),
            isDeleted = false,
            mutableSetOf(USER)
        )
    }

    fun newStandardAccount() = Account(
        id = AccountId(value = UUID.randomUUID().toString()),
        type = AccountType.STANDARD,
        createdAt = LocalDateTime.now(),
        usage = Usage(
            count = 0L,
            credit = 0L
        ),
        isBlocked = false,
        isDeleted = false
    )
}
