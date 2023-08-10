package authentication.impl

import authentication.domain.Messages.USUARIO_CONTA_BLOQUEADA
import authentication.domain.Messages.USUARIO_CONTA_DELETADA
import authentication.domain.Messages.USUARIO_DELETADO
import authentication.domain.Messages.USUARIO_NAO_TEM_CREDITOS
import authentication.domain.repository.UserRepository
import model.api.Access
import model.api.event.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationAuthenticatorTest {

    private val publisher: Publisher<DomainEvent, Subscriber<DomainEvent>> = mock(Publisher::class.java) as Publisher<DomainEvent, Subscriber<DomainEvent>>
    private val userRepository = mock(UserRepository::class.java)

    private val applicationAuthenticator = ApplicationAuthenticator(
        publisher = publisher,
        userRepository = userRepository
    )

    @Test
    fun ` when user authenticate should fire event `() {
        createUserWithCredit().let { user ->
            createPassword().let { password ->
                `when`(userRepository.find(user.id, password)).thenReturn(user)
                applicationAuthenticator.authenticate(user.id, password)
                verify(publisher, times(1)).publish(any(UserAuthenticatedDomainEvent::class.java))
            }
        }
    }

    @Test
    fun ` when user does not authenticate should not fire event `() {
        createUserWithCredit().let { it ->
            createPassword().let { password ->
                it.zeroCredits().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    applicationAuthenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
                it.delete().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    applicationAuthenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
                it.deleteAccount().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    applicationAuthenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
                it.blockAccount().let { user ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    applicationAuthenticator.authenticate(user.id, password)
                    verify(publisher, times(0)).publish(any(UserAuthenticatedDomainEvent::class.java))
                }
            }
        }
    }

    @Test
    fun ` when register user access should fire event `() {
        createUserWithCredit().let { user ->
            Access().let { access ->
                applicationAuthenticator.registerUserAccess(user, access)
                verify(publisher, times(1)).publish(any(UserAccessRegisteredDomainEvent::class.java))
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
                    applicationAuthenticator.authenticate(userWithNoCredit.id, password).let { authentication ->
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
                    applicationAuthenticator.authenticate(deletedUser.id, password).let { authentication ->
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
                    applicationAuthenticator.authenticate(userWithDeletedAccount.id, password).let { authentication ->
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
                    applicationAuthenticator.authenticate(userWithBlockedAccount.id, password).let { authentication ->
                        assertFalse { authentication.isAuthenticated }
                        assertFalse { authentication.errorMessages.isEmpty() }
                        assertEquals(USUARIO_CONTA_BLOQUEADA, authentication.errorMessages[0])
                    }
                }
            }
    }

    @Test
    fun ` when user is OK should authenticate `() {
        createUserWithCredit()
            .let { user ->
                createPassword().let { password ->
                    `when`(userRepository.find(user.id, password)).thenReturn(user)
                    applicationAuthenticator.authenticate(user.id, password).let { authentication ->
                        assertTrue { authentication.isAuthenticated }
                        assertTrue { authentication.errorMessages.isEmpty() }
                    }
                }
            }
    }

}

private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)