package authentication.impl

import authentication.app.Factory
import authentication.app.InitialAction.SET_INITIAL_CREDIT_FOR_STADARD_ACCOUNT
import authentication.domain.Messages
import authentication.domain.Publisher
import authentication.domain.repository.UserRepository
import model.Password
import model.User
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.*
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
        createUserWithInitialCredit().let { userOK ->
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
        createUserWithInitialCredit()
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
        createUserWithInitialCredit()
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
        createUserWithInitialCredit()
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
        with(createUserWithInitialCredit()) {
            zerarCreditos().let { user ->
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
        with(createUserWithInitialCredit()) {
            val password = createPassword()
            zerarCreditos().let { user ->
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

}

private fun createUserWithInitialCredit(): User = factory.newUser(
    name = UUID.randomUUID().toString(),
    email = UUID.randomUUID().toString(),
    SET_INITIAL_CREDIT_FOR_STADARD_ACCOUNT
)

private fun createPassword() =
    Password(UUID.randomUUID().toString())
