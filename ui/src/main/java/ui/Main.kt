package ui

import authentication.Authenticator
import model.Email

class Main {
}


fun main() {
    UiAuthenticator().authenticate(Email("serse"))
}

class UiAuthenticator : Authenticator {
    override fun authenticate(email: Email) {
        println("This is authenticator ... ")
    }
}