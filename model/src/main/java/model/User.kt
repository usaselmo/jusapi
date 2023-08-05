package model

import java.time.LocalDateTime

data class UserId(
    val value: String,
)


data class User(
    val id: UserId,
    val name: Name,
    val email: Email,
    val createdAt: LocalDateTime,
    private val account: Account,
    val isDeleted: Boolean,
) {

    fun delete() =
        copy(isDeleted = true, account = account.copy(isDeleted = true, isBlocked = true))

    fun deleteAccount() =
        copy(account = account.copy(isBlocked = true, isDeleted = true))

    fun blockAccount() =
        copy(account = account.copy(isBlocked = true))

    fun incrementAccessCount(): User {
        return copy(account = account.incrementAccessCount())
    }

    fun balance(): Long =
        account.balance()

    fun increaseBalance(ammount: Long): User =
        copy(account = account.increaseCredit(ammount))

    fun hasNoBalance(): Boolean =
        account.balance() <= 0L

    fun accountIsBlocked() =
        account.isBlocked

    fun accountIsDeleted() =
        account.isDeleted

    fun setInitialCredit(): User =
        copy(account = account.copy(usage = account.usage.copy(credit = 1000L)))

    fun zerarCreditos(): User =
        copy(account = account.copy(usage = account.usage.copy(credit = 0L)))

}

data class Account(
    val id: AccountId,
    val type: AccountType,
    val createdAt: LocalDateTime,
    val usage: Usage,
    val isBlocked: Boolean,
    val isDeleted: Boolean,
) {
    fun increaseCredit(ammount: Long): Account =
        copy(usage = usage.copy(credit = usage.credit + ammount))

    fun balance() =
        usage.credit - usage.count

    fun incrementAccessCount(): Account {
        return copy(usage = usage.copy(count = usage.count + 1))
    }
}

data class AccountId(
    val value: String,
)

enum class AccountType {
    STANDARD
}

data class Usage(
    val count: Long,
    val credit: Long,
)

class Access {

}