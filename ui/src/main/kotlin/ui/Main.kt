package ui

import authentication.api.Authenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(value = [
    "legis",
    "authentication",
    "ui"
])
class Main : CommandLineRunner {

    @Autowired
    lateinit var authenticator: Authenticator

    override fun run(vararg args: String?) {
        authenticator.authenticate()
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}

