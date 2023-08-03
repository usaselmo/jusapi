package legis

import authentication.api.Authenticator

class Main {
}

fun main() {
    LegisAuthenticator().authenticate()
}

class LegisAuthenticator : Authenticator {
    override fun authenticate() {
        println("legis ....")
        //TODO("Not yet implemented")
    }
}