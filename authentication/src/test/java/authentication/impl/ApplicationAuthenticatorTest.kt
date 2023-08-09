package authentication.impl

import authentication.app.Factory
import authentication.domain.Messages
import authentication.domain.Publisher
import authentication.domain.repository.UserRepository
import model.Access
import model.AccountType
import model.Password
import model.User
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

private val publisher: Publisher = mock(Publisher::class.java)
private val factory = Factory(publisher = publisher)

class ApplicationAuthenticatorTest {

    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val authenticator = ApplicationAuthenticator(
        publisher = publisher,
        userRepository = userRepository
    )

    @Test
    fun ` quando usuario nao bloqueado deve autenticar `() {
        createUserWithCredit().let { userOK ->
            createPassword().let { password ->
                `when`(userRepository.find(userOK.email, password)).thenReturn(userOK)
                authenticator.authenticate(userOK.email, password).let { authentication ->
                    assertTrue { authentication.isAuthenticated }
                    assertTrue { authentication.errorMessages.isEmpty() }
                }
            }
        }
    }

    @Test
    fun ` quando usuario deletado nao deve autenticar `() {
        createUserWithCredit()
            .delete().let { userDeleted ->
                createPassword().let { password ->
                    `when`(userRepository.find(userDeleted.email, password)).thenReturn(userDeleted)
                    authenticator.authenticate(userDeleted.email, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertSame(Messages.USUARIO_DELETADO, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` quando usuario conta bloqueada nao deve autenticar `() {
        createUserWithCredit()
            .blockAccount().let { userWithAccountBlocked ->
                createPassword().let { password ->
                    `when`(userRepository.find(userWithAccountBlocked.email, password)).thenReturn(userWithAccountBlocked)
                    authenticator.authenticate(userWithAccountBlocked.email, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertSame(Messages.USUARIO_CONTA_BLOQUEADA, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` quando usuario conta deletada nao deve autenticar `() {
        createUserWithCredit()
            .deleteAccount().let { userWithNoAccount ->
                createPassword().let { password ->
                    `when`(userRepository.find(userWithNoAccount.email, password)).thenReturn(userWithNoAccount)
                    authenticator.authenticate(userWithNoAccount.email, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertSame(Messages.USUARIO_CONTA_DELETADA, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` quando usuario conta sem credito nao deve autenticar `() {
        with(createUserWithCredit()) {
            zeroCredits().let { user ->
                createPassword().let { password ->
                    `when`(userRepository.find(user.email, password)).thenReturn(user)
                    authenticator.authenticate(user.email, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertEquals(Messages.USUARIO_NAO_TEM_CREDITOS, authentication.errorMessages[0])
                    }
                }
            }
        }
    }

    @Test
    fun ` quando usuario renovar credito deve autenticar `() {
        with(createUserWithCredit()) {
            val password = createPassword()
            zeroCredits().let { user ->
                `when`(userRepository.find(user.email, password)).thenReturn(user)
                authenticator.authenticate(user.email, password).let { authentication ->
                    assertFalse { authentication.isAuthenticated }
                    assertEquals(Messages.USUARIO_NAO_TEM_CREDITOS, authentication.errorMessages[0])
                }
            }
            increaseBalance(1000L).let { user ->
                `when`(userRepository.find(user.email, password)).thenReturn(user)
                authenticator.authenticate(user.email, password).let { authentication ->
                    assertTrue { authentication.isAuthenticated }
                    assertTrue(authentication.errorMessages.isEmpty())
                }
            }
        }
    }

    @Test
    fun ` quando poe credito o saldo aumenta `() {
        with(createUserWithCredit()) {
            val initialBalance = balance()
            assertEquals(initialBalance + 100L, increaseBalance(100L).balance())
            assertEquals(0L, zeroCredits().balance())
        }
    }

    @Test
    fun ` quando acessa o sistema o saldo diminui `() {
        with(createUserWithCredit()) {
            val initialBalance = balance()
            val endBalance = this.registerAccess() // 1
                .registerAccess() // 2
                .registerAccess() // 3
                .balance()
            assertEquals(initialBalance - 3L, endBalance)
        }
    }


    @Test
    fun ` quando usa todo o credito o saldo zera `() {
        with(createUserWithCredit()) {
            val balance = zeroCredits()
                .increaseBalance(5L)
                .registerAccess() // 1
                .registerAccess() // 2
                .registerAccess() // 3
                .registerAccess() // 4
                .registerAccess() // 5
                .balance()
            assertEquals(0L, balance)
        }
    }

    @Test
    fun ` deve definir credito inicial `() {
        factory.newUser(
            name = UUID.randomUUID().toString(),
            email = UUID.randomUUID().toString()
        ) {
            it.setInitialCredit()
        }.let {
            assertEquals(AccountType.STANDARD.initialCredit, it.balance())
        }
        factory.newUser(
            name = UUID.randomUUID().toString(),
            email = UUID.randomUUID().toString(),
        ).let {
            assertEquals(0L, it.balance())
        }
    }

    @Test
    fun ` credito inicial deve ser zero`() {
        factory.newUser(
            name = UUID.randomUUID().toString(),
            email = UUID.randomUUID().toString()
        ).let {
            assertEquals(0L, it.balance())
        }

    }

    @Test
    fun ` quando registrar acesso saldo deve diminuir `() {
        createUserWithCredit().let { user ->
            assertEquals(1L, user.balance() - authenticator.registerUserAccess(user, Access()).balance())
        }
    }

}

private fun createUserWithCredit(): User = factory.newUser(
    name = UUID.randomUUID().toString(),
    email = UUID.randomUUID().toString()
){
    it.increaseBalance(Random.nextInt(5..90).toLong())
}

private fun createPassword() =
    Password(UUID.randomUUID().toString())
