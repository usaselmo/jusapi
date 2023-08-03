package authentication.domain.model

import authentication.api.Email
import authentication.api.UserId
import authentication.domain.vo.Name
import java.time.LocalDateTime

data class User(
    val id: UserId,
    val name: Name,
    val email: Email,
    val createdAt: LocalDateTime,
    val account: Account,
    val isDeleted: Boolean,
) {

    fun delete() =
        copy(isDeleted = true, account = account.copy(isDeleted = true, isBlocked = true))

    fun deleteCurrentAccount() =
        copy(account = account.copy(isBlocked = true, isDeleted = true))

    fun incrementAccessCount(): User {
        val inc = account.usage.count + 1
        return copy(account = account.copy(usage = account.usage.copy(count = inc)))
    }

    fun hasFunctionalAccount(): Boolean =
        !account.isDeleted && !account.isBlocked

    fun accountType(): AccountType =
        account.type

    fun accountIsBlocked() =
        account.isBlocked

    fun accountIsDeleted() =
        account.isDeleted

    fun usageQuantity() =
        account.usage.count

}

data class Account(
    val id: AccountId,
    val type: AccountType,
    val createdAt: LocalDateTime,
    val usage: Usage,
    val isBlocked: Boolean,
    val isDeleted: Boolean,
)

data class AccountId(
    val value: String,
)

enum class AccountType {
    STANDARD
}

data class Usage(
    val count: Long,
)