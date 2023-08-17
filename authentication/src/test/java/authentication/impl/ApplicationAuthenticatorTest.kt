package authentication.impl

import authentication.app.Factory
import authentication.domain.AuthenticationException
import authentication.domain.Messages.ERROR_AO_AUTENTICAR_USUARIO
import authentication.domain.Messages.USUARIO_NAO_ENCONTRADO
import authentication.domain.Messages.USUARIO_CONTA_BLOQUEADA
import authentication.domain.Messages.USUARIO_CONTA_DELETADA
import authentication.domain.Messages.USUARIO_DELETADO
import authentication.domain.Messages.USUARIO_NAO_TEM_CREDITOS
import authentication.domain.repository.UserRepository
import core.api.event.*
import core.api.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

val factory = Factory()

@Suppress("UNCHECKED_CAST")
class ApplicationAuthenticatorTest {

    private val publisher = mock(Publisher::class.java) as Publisher
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val authenticator = ApplicationAuthenticator(
        publisher = publisher,
        userRepository = userRepository
    )

    @Test
    fun ` when user authentication error should fire AuthenticationException `() {
        createUserWithCredit().let { user ->
            createPassword().let { password ->
                `when`(userRepository.find(user.id, password)).thenReturn(user)
                `when`(publisher.publish(any(DomainEvent::class.java))).then { throw Exception() }
                assertThrows<AuthenticationException> {
                    authenticator.authenticate(user.id, password)
                }
            }
        }
    }

    @Test
    fun ` when user authenticate should fire event `() {
        createUserWithCredit().let { user ->
            createPassword().let { password ->
                `when`(userRepository.find(user.id, password)).thenReturn(user)
                authenticator.authenticate(user.id, password)
                verify(publisher, times(1)).publish(any(UserAuthenticatedDomainEvent::class.java))
            }
        }
    }

    @Test
    fun ` when user not found should throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            createPassword().let { password ->
                `when`(userRepository.find(user.id, password)).thenReturn(null)
                assertThrows<AuthenticationException> (USUARIO_NAO_ENCONTRADO){
                    authenticator.authenticate(user.id, password)
                }
            }
        }
    }

    @Test
    fun ` when user is OK should authenticate `() {
        createUserWithCredit().let { user ->
            createPassword().let { password ->
                `when`(userRepository.find(user.id, password)).thenReturn(user)
                authenticator.authenticate(user.id, password).let { authentication ->
                    assertTrue { authentication.isAuthenticated }
                    assertTrue { authentication.errorMessages.isEmpty() }
                }
            }
        }
    }

    @Test
    fun ` when user does not authenticate should not fire event `() {
        createUserWithCredit().let { it ->
            createPassword().let { password ->
                it.zeroCredits().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    authenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
                it.delete().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    authenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
                it.deleteAccount().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    authenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
                it.blockAccount().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    authenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
            }
        }
    }

    @Test
    fun ` when register user access should fire event `() {
        createUserWithCredit().let { user ->
            Access().let { access ->
                authenticator.registerUserAccess(user, access)
                verify(publisher, times(1)).publish(any(UserAccessRegisteredDomainEvent::class.java))
            }
        }
    }

    @Test
    fun ` when register user access fails should Throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            Access().let { access ->
                `when`(publisher.publish(any(DomainEvent::class.java))).then { throw Exception() }
                assertThrows <AuthenticationException>(ERROR_AO_AUTENTICAR_USUARIO){
                    authenticator.registerUserAccess(user, access)
                }
            }
        }
    }

    @Test
    fun ` when user has no credit should return USUARIO_NAO_TEM_CREDITOS `() {
        createUserWithCredit()
            .zeroCredits()
            .let { userWithNoCredit ->
                createPassword().let { password ->
                    `when`(userRepository.find(userWithNoCredit.id, password)).thenReturn(userWithNoCredit)
                    authenticator.authenticate(userWithNoCredit.id, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertFalse { authentication.errorMessages.isEmpty() }
                        assertEquals(USUARIO_NAO_TEM_CREDITOS, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` when user is deleted should return USUARIO_DELETADO `() {
        createUserWithCredit()
            .delete()
            .let { deletedUser ->
                createPassword().let { password ->
                    `when`(userRepository.find(deletedUser.id, password)).thenReturn(deletedUser)
                    authenticator.authenticate(deletedUser.id, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertFalse { authentication.errorMessages.isEmpty() }
                        assertEquals(USUARIO_DELETADO, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` when user account is deleted should return USUARIO_CONTA_DELETADA `() {
        createUserWithCredit()
            .deleteAccount()
            .let { userWithDeletedAccount ->
                createPassword().let { password ->
                    `when`(userRepository.find(userWithDeletedAccount.id, password)).thenReturn(userWithDeletedAccount)
                    authenticator.authenticate(userWithDeletedAccount.id, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertFalse { authentication.errorMessages.isEmpty() }
                        assertEquals(USUARIO_CONTA_DELETADA, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` when user account is blocked should return USUARIO_CONTA_BLOQUEADA `() {
        createUserWithCredit()
            .blockAccount()
            .let { userWithBlockedAccount ->
                createPassword().let { password ->
                    `when`(userRepository.find(userWithBlockedAccount.id, password)).thenReturn(userWithBlockedAccount)
                    authenticator.authenticate(userWithBlockedAccount.id, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertFalse { authentication.errorMessages.isEmpty() }
                        assertEquals(USUARIO_CONTA_BLOQUEADA, authentication.errorMessages[0])
                    }
                }
            }
    }


    /**
     *
     */


    @Test
    fun ` when authetication fails should throw AuthenticationException `() {
        createUserWithCredit().let { userOK ->
            createPassword().let { password ->
                `when`(userRepository.find(userOK.email, password)).then { throw Exception() }
                assertThrows<AuthenticationException>(ERROR_AO_AUTENTICAR_USUARIO) {
                    authenticator.authenticate(userOK.email, password)
                }
            }
        }
    }
    @Test
    fun ` quando usuario nao encontrado deve lancar AuthenticationException  `() {
        createUserWithCredit().let { userOK ->
            createPassword().let { password ->
                `when`(userRepository.find(userOK.email, password)).thenReturn(null)
                assertThrows<AuthenticationException>(USUARIO_NAO_ENCONTRADO) {
                    authenticator.authenticate(userOK.email, password)
                }
            }
        }
    }

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
                        assertSame(USUARIO_DELETADO, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` quando usuario conta bloqueada nao deve autenticar `() {
        createUserWithCredit()
            .blockAccount().let { userWithAccountBlocked ->
                createPassword().let { password ->
                    `when`(userRepository.find(userWithAccountBlocked.email, password)).thenReturn(
                        userWithAccountBlocked
                    )
                    authenticator.authenticate(userWithAccountBlocked.email, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertSame(USUARIO_CONTA_BLOQUEADA, authentication.errorMessages[0])
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
                        assertSame(USUARIO_CONTA_DELETADA, authentication.errorMessages[0])
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
                        assertEquals(USUARIO_NAO_TEM_CREDITOS, authentication.errorMessages[0])
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
                    assertEquals(USUARIO_NAO_TEM_CREDITOS, authentication.errorMessages[0])
                }
            }
            increaseBalance(Credit.withDefaults(1000L)).let { user ->
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
            assertEquals(initialBalance + 100L, increaseBalance(Credit.withDefaults(100L)).balance())
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
                .increaseBalance(Credit.withDefaults(5L))
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
            email = createRandomEmail()
        ) {
            it.setInitialCredit()
        }.let {
            assertEquals(AccountType.STANDARD.initialCredit, it.balance())
        }
        factory.newUser(
            name = UUID.randomUUID().toString(),
            email = createRandomEmail(),
        ).let {
            assertEquals(0L, it.balance())
        }
    }

    @Test
    fun ` credito inicial deve ser zero`() {
        factory.newUser(
            name = UUID.randomUUID().toString(),
            email = createRandomEmail()
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

fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

fun createUserWithCredit(): User = factory.newUser(
    name = UUID.randomUUID().toString(),
    email = createRandomEmail()
) {
    it.increaseBalance(Credit.withDefaults(Random.nextInt(5..90).toLong()))
}

fun createRandomEmail() =
    "${UUID.randomUUID()}@${UUID.randomUUID()}.com"

fun createPassword() =
    Password(UUID.randomUUID().toString())
