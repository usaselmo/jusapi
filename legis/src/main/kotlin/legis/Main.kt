package legis

import authentication.api.Authenticator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Main {
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
    LegisAuthenticator().authenticate()
}

class LegisAuthenticator : Authenticator {
    override fun authenticate() {
        println("legis ....")
        //TODO("Not yet implemented")
    }
}