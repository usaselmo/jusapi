package core.api.model

import java.time.LocalDateTime

data class UserId(
    val value: String,
)

enum class Role {
    ADMIN, FINANCIAL_ADMIN,
    USER, FINANCIAL_USER,
    GUEST
}

data class User(
    val id: UserId,
    val name: Name,
    val email: Email,
    val createdAt: LocalDateTime,
    private val account: Account,
    val isDeleted: Boolean,
    private val roles: MutableSet<Role>
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

    fun increaseBalance(credit: Credit): User =
        copy(account = account.increaseCredit(credit))

    fun hasNoBalance(): Boolean =
        account.balance() <= 0L

    fun accountIsBlocked(): Boolean =
        account.isBlocked

    fun accountIsDeleted(): Boolean =
        account.isDeleted

    fun setInitialCredit(): User {
        return copy(account = account.setInitialCredit())
    }

    fun removeAllCredits(): User =
        copy(account = account.removeAllCredits())

    fun addRole(role: Role): User {
        roles.add(role)
        return this;
    }

    fun roles(): List<Role> =
        listOf(*roles.toTypedArray())

    fun hasRole(role: Role) : Boolean =
        roles.contains(role)

}

data class Account(
    val id: AccountId,
    val type: AccountType,
    val createdAt: LocalDateTime,
    val usage: Usage,
    val isBlocked: Boolean,
    val isDeleted: Boolean,
) {
    fun increaseCredit(credit: Credit): Account =
        copy(usage = usage.copy(credit = usage.credit + credit.value.value))

    fun balance(): Long =
        usage.credit - usage.count

    fun incrementAccessCount(): Account {
        return copy(usage = usage.incrementAccessCount())
    }

    fun setInitialCredit(): Account =
        copy(usage = usage.copy(credit = type.initialCredit))

    fun removeAllCredits() =
        copy(usage = usage.copy(credit = 0L))

    fun delete(): Account =
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
) {
    fun incrementAccessCount(): Usage =
        copy(count = count + 1)
}

class Access {
}

data class Credit(
    val value: Money,
    val expires: Boolean
) {
    companion object {
        fun withDefaults(amount: Long) =
            Credit(value = Money(amount, 0L), expires = false)
    }
}

data class Money(
    val value: Long,
    val cents: Long
)