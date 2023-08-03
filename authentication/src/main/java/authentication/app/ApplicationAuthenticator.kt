package authentication.app

import authentication.api.Authentication
import authentication.api.Authenticator
import authentication.api.Email
import authentication.api.UserId
import authentication.domain.EventPublisher
import authentication.domain.model.User
import authentication.domain.vo.Access

class ApplicationAuthenticator(
    private val eventPublisher: EventPublisher,
) : Authenticator {

    fun registerUserAccess(user: User, access: Access): User {
        return user.incrementAccessCount().also {
            eventPublisher.publish(
                UserAccessRegisteredDomainEvent(user = user, access = access)
            )
        }
    }

    fun authenticate(userId: UserId, password: String): Authentication {
        return Authentication(true) //        TODO("Not yet implemented")
    }

    fun authenticate(email: Email, password: String): Authentication {
        return Authentication(true) //        TODO("Not yet implemented")
    }

    override fun authenticate() {
        println("This is authenticator ....") //TODO("Not yet implemented")
    }

}