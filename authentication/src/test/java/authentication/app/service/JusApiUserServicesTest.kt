package authentication.app.service

import authentication.api.UserRegistrationInput
import authentication.app.Factory
import authentication.domain.AuthenticationException
import authentication.domain.Messages
import authentication.domain.Messages.ERROR_AO_BLOQUEAR_CONTA_DE_USUARIO
import authentication.domain.Messages.ERROR_AO_BLOQUEAR_USUARIO
import authentication.domain.Messages.ERROR_AO_DELETAR_USUARIO
import authentication.domain.Messages.USUARIO_NAO_ENCONTRADO
import authentication.domain.repository.UserRepository
import authentication.impl.any
import authentication.impl.createPassword
import authentication.impl.createRandomEmail
import authentication.impl.createUserWithCredit
import core.api.model.Credit
import core.api.event.Publisher
import core.api.event.UserCreatedDomainEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

@Suppress("UNCHECKED_CAST")
class JusApiUserServicesTest {

    private val mockUserRepository: UserRepository = mock(UserRepository::class.java)
    private val mockPublisher = mock(Publisher::class.java) as Publisher
    private val mockFactory = mock(Factory::class.java)
    private val factory = Factory()
    private val jusApiUserServices = JusApiUserServices(
        mockUserRepository, mockPublisher, mockFactory
    )

    @Test
    fun register() {
        val user = factory.newUser(UUID.randomUUID().toString(), createRandomEmail()).let { user ->
            createPassword().let { password ->
                UserRegistrationInput(user.name, user.email, password, Credit.withDefaults(10L)).let { urr->

                    `when`(mockFactory.newUser(anyString(), anyString(), any())).thenReturn(user)
                    doNothing().`when`(mockUserRepository).update(user)
                    doNothing().`when`(mockPublisher).publish(any(UserCreatedDomainEvent::class.java))

                    jusApiUserServices.signup(urr)

                    verify(mockFactory, times(1)).newUser(anyString(), anyString(), any())
                    verify(mockUserRepository, times(1)).signup(user, password)
                    verify(mockPublisher, times(1)).publish(any(UserCreatedDomainEvent::class.java))
                }
            }
        }
    }

    @Test
    fun ` register should throw AuthenticationException `() {
        factory.newUser(UUID.randomUUID().toString(), createRandomEmail()).let { user ->
            UserRegistrationInput(user.name, user.email, createPassword(), Credit.withDefaults(10L)).let { urr ->
                `when`(mockFactory.newUser(anyString(), anyString(), any())).then { throw Exception("") }
                assertThrows<AuthenticationException> { jusApiUserServices.signup(urr) }
            }
        }
    }

    @Test
    fun ` register should throw AuthenticationException 2 `() {
        factory.newUser(UUID.randomUUID().toString(), createRandomEmail()).let { user ->
            createPassword().let { password ->
                UserRegistrationInput(user.name, user.email, password, Credit.withDefaults(10L)).let { urr ->
                    `when`(mockFactory.newUser(anyString(), anyString(), any())).thenReturn(user)
                    `when`(mockUserRepository.signup(user, password)).then { throw Exception() }

                    assertThrows<AuthenticationException> { jusApiUserServices.signup(urr) }
                }
            }
        }
    }

    @Test
    fun ` register should throw AuthenticationException 3 `() {
        factory.newUser(UUID.randomUUID().toString(), createRandomEmail()).let { user ->
            UserRegistrationInput(user.name, user.email, createPassword(), Credit.withDefaults(10L)).let { urr ->
                `when`(mockFactory.newUser(anyString(), anyString(), any())).thenReturn(user)
                doNothing().`when`(mockUserRepository).update(user)
                `when`(mockPublisher.publish(any(UserCreatedDomainEvent::class.java))).then { throw Exception() }

                assertThrows<AuthenticationException> { jusApiUserServices.signup(urr) }
            }
        }
    }

    @Test
    fun increaseBalance() {
        Credit.withDefaults(10L).let { credit ->
            createUserWithCredit()
                .zeroCredits().let { user ->
                    `when`(mockUserRepository.find(user.id)).thenReturn(user)

                    jusApiUserServices.increaseBalance(user.id, credit)

                    val userWithIncreasedBalance = user.increaseBalance(credit)
                    verify(mockUserRepository, times(1)).find(user.id)
                    verify(mockUserRepository, never()).update(user)
                    verify(mockUserRepository, times(1)).update(userWithIncreasedBalance)
                }
        }
    }

    @Test
    fun ` increaseBalance should throw AuthenticationException `() {
        Credit.withDefaults(10L).let { credit ->
            createUserWithCredit()
                .zeroCredits().let { user ->
                    `when`(mockUserRepository.find(user.id)).then { throw Exception() }

                    assertThrows<AuthenticationException>(Messages.ERROR_AO_AUMENTAR_CREDITO_DE_USUARIO) {
                        jusApiUserServices.increaseBalance(user.id, credit)
                    }
                }
        }
    }

    @Test
    fun ` increase balance should throw AuthenticationException ERROR_USER_NOT_FOUND `() {
        Credit.withDefaults(10L).let { credit ->
            createUserWithCredit()
                .zeroCredits().let { user ->
                    `when`(mockUserRepository.find(user.id)).then { null }

                    assertThrows<AuthenticationException>(USUARIO_NAO_ENCONTRADO) {
                        jusApiUserServices.increaseBalance(user.id, credit)
                    }
                }
        }
    }

    @Test
    fun ` delete `() {
        createUserWithCredit().let { user ->
            user.delete().let { userDeleted ->
                `when`(mockUserRepository.find(user.id)).thenReturn(user)
                doNothing().`when`(mockUserRepository).update(userDeleted)

                jusApiUserServices.delete(user.id)

                verify(mockUserRepository, times(1)).find(user.id)
                verify(mockUserRepository, never()).update(user)
                verify(mockUserRepository, times(1)).update(userDeleted)
            }
        }
    }

    @Test
    fun ` delete user should throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            `when`(mockUserRepository.find(user.id)).then { throw Exception() }

            assertThrows<AuthenticationException>(ERROR_AO_DELETAR_USUARIO) {
                jusApiUserServices.delete(user.id)
            }
        }
    }

    @Test
    fun ` block `() {
        createUserWithCredit().let { user ->
            user.blockAccount().let { blockedUser ->
                `when`(mockUserRepository.find(user.id)).thenReturn(user)
                doNothing().`when`(mockUserRepository).update(user)

                jusApiUserServices.block(user.id)

                verify(mockUserRepository, times(1)).find(user.id)
                verify(mockUserRepository, never()).update(user)
                verify(mockUserRepository, times(1)).update(blockedUser)
            }
        }
    }

    @Test
    fun ` block should throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            `when`(mockUserRepository.find(user.id)).then { throw Exception() }

            assertThrows<AuthenticationException>(ERROR_AO_BLOQUEAR_USUARIO) {
                jusApiUserServices.block(user.id)
            }
        }
    }

    @Test
    fun ` block should throw AuthenticationException 2 `() {
        createUserWithCredit().let { user ->
            user.blockAccount().let { blockedUser ->
                `when`(mockUserRepository.find(user.id)).thenReturn(user)
                `when`(mockUserRepository.update(blockedUser)).then { throw Exception() }

                assertThrows<AuthenticationException>(ERROR_AO_BLOQUEAR_USUARIO) {
                    jusApiUserServices.block(user.id)
                }
            }
        }
    }

    @Test
    fun ` block should throw AuthenticationException ERROR_USER_NOT_FOUND `() {
        createUserWithCredit().let { user ->
            `when`(mockUserRepository.find(user.id)).thenReturn(null)

            assertThrows<AuthenticationException>(USUARIO_NAO_ENCONTRADO) {
                jusApiUserServices.block(user.id)
            }
        }
    }

    @Test
    fun ` deleteAccount`() {
        createUserWithCredit().let { user ->
            user.deleteAccount().let { userAccountDeleted ->

                `when`(mockUserRepository.find(user.id)).then { user }

                jusApiUserServices.deleteAccount(user.id)

                verify(mockUserRepository, times(1)).find(user.id)
                verify(mockUserRepository, never()).update(user)
                verify(mockUserRepository, times(1)).update(userAccountDeleted)
            }
        }
    }

    @Test
    fun ` when deleteAccount user not found should throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            user.deleteAccount().let {

                `when`(mockUserRepository.find(user.id)).then { null }

                assertThrows<AuthenticationException>(USUARIO_NAO_ENCONTRADO) {
                    jusApiUserServices.deleteAccount(user.id)
                }

            }
        }
    }

    @Test
    fun ` when deleteAccount fails should throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            user.deleteAccount().let { userAccountDeleted ->

                `when`(mockUserRepository.find(user.id)).then { throw Exception() }

                assertThrows<AuthenticationException> {
                    jusApiUserServices.deleteAccount(user.id)
                }
            }
        }
    }

    @Test
    fun ` blockAccount`() {
        createUserWithCredit().let { user ->
            user.blockAccount().let { userAccountBlocked ->

                `when`(mockUserRepository.find(user.id)).then { user }

                jusApiUserServices.blockAccount(user.id)

                verify(mockUserRepository, times(1)).find(user.id)
                verify(mockUserRepository, never()).update(user)
                verify(mockUserRepository, times(1)).update(userAccountBlocked)
            }
        }
    }

    @Test
    fun ` when blockAccount user not found should throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            user.blockAccount().let {

                `when`(mockUserRepository.find(user.id)).then { null }

                assertThrows<AuthenticationException>(USUARIO_NAO_ENCONTRADO) {
                    jusApiUserServices.blockAccount(user.id)
                }
            }
        }
    }

    @Test
    fun ` when blockAccount fails should throw AuthenticationException `() {
        createUserWithCredit().let { user ->
            user.blockAccount().let {

                `when`(mockUserRepository.find(user.id)).then { throw Exception() }

                assertThrows<AuthenticationException>(ERROR_AO_BLOQUEAR_CONTA_DE_USUARIO) {
                    jusApiUserServices.blockAccount(user.id)
                }
            }
        }
    }


}