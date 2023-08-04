package ui

import authentication.Authenticator
import model.Email
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Main


fun main(args: Array<String>) {
    UiAuthenticator().authenticate(Email("serse"))
    runApplication<Main>(*args)
}

class UiAuthenticator : Authenticator {
    override fun authenticate(email: Email) {
        println("This is authenticator ... ")
    }
}