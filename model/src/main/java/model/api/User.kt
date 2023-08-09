package model.api

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

    fun delete(): User =
        copy(isDeleted = true, account = account.delete())

    fun deleteAccount(): User =
        copy(account = account.delete())

    fun blockAccount(): User =
        copy(account = account.copy(isBlocked = true))

    fun registerAccess(): User =
        copy(account = account.incrementAccessCount())

    fun balance(): Long =
        account.balance()

    fun increaseBalance(ammount: Long): User =
        copy(account = account.increaseCredit(ammount))

    fun hasNoBalance(): Boolean =
        account.balance() <= 0L

    fun accountIsBlocked(): Boolean =
        account.isBlocked

    fun accountIsDeleted(): Boolean =
        account.isDeleted

    fun setInitialCredit(): User {
        return copy(account = account.setInitialCredit())
    }

    fun zeroCredits(): User =
        copy(account = account.zeroCredits())

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

    fun balance(): Long =
        usage.credit - usage.count

    fun incrementAccessCount(): Account {
        return copy(usage = usage.copy(count = usage.count + 1))
    }

    fun setInitialCredit(): Account =
        copy(usage = usage.copy(credit = type.initialCredit))

    fun zeroCredits(): Account =
        copy(usage = usage.copy(credit = 0L))

    fun delete() : Account =
        copy(isDeleted = true, isBlocked = true)
}

data class AccountId(
    val value: String,
)

enum class AccountType(val initialCredit: Long) {
    STANDARD(1000L);

}

data class Usage(
    val count: Long,
    val credit: Long,
)

class Access {

}