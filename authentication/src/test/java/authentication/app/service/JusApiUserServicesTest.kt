package authentication.app.service

import authentication.api.UserRegistrationRequest
import authentication.app.Factory
import authentication.domain.AuthenticationException
import authentication.domain.Messages
import authentication.domain.repository.UserRepository
import authentication.impl.any
import authentication.impl.createPassword
import authentication.impl.createRandomEmail
import authentication.impl.createUserWithCredit
import model.api.Credit
import model.api.event.DomainEvent
import model.api.event.Publisher
import model.api.event.Subscriber
import model.api.event.UserCreatedDomainEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class JusApiUserServicesTest {

    private val mockUserRepository: UserRepository = mock(UserRepository::class.java)
    private val mockPublisher = mock(Publisher::class.java) as Publisher<DomainEvent, Subscriber<DomainEvent>>
    private val mockFactory = mock(Factory::class.java)
    private val factory = Factory()
    private val jusApiUserServices = JusApiUserServices(
        mockUserRepository, mockPublisher, mockFactory
    )

    @Test
    fun register() {
        val user = factory.newUser(UUID.randomUUID().toString(), createRandomEmail())
        val urr = UserRegistrationRequest(user.name, user.email, createPassword(), Credit.withDefaults(10L))
        `when`(mockFactory.newUser(anyString(), anyString(), any())).thenReturn(user)
        doNothing().`when`(mockUserRepository).save(user)
        doNothing().`when`(mockPublisher).publish(any(UserCreatedDomainEvent::class.java))

        jusApiUserServices.register(urr)

        verify(mockUserRepository, times(1)).save(user)
        verify(mockFactory, times(1)).newUser(anyString(), anyString(), any())
        verify(mockPublisher, times(1)).publish(any(UserCreatedDomainEvent::class.java))
    }

    @Test
    fun ` register should throw AuthenticationException `() {
        factory.newUser(UUID.randomUUID().toString(), createRandomEmail()).let { user ->
            UserRegistrationRequest(user.name, user.email, createPassword(), Credit.withDefaults(10L)).let { urr ->
                `when`(mockFactory.newUser(anyString(), anyString(), any())).then { throw Exception("") }
                assertThrows<AuthenticationException> { jusApiUserServices.register(urr) }
            }
        }
    }

    @Test
    fun ` register should throw AuthenticationException 2 `() {
        factory.newUser(UUID.randomUUID().toString(), createRandomEmail()).let { user ->
            UserRegistrationRequest(user.name, user.email, createPassword(), Credit.withDefaults(10L)).let { urr ->
                `when`(mockFactory.newUser(anyString(), anyString(), any())).thenReturn(user)
                `when`(mockUserRepository.save(user)).then { throw Exception() }

                assertThrows<AuthenticationException> { jusApiUserServices.register(urr) }
            }
        }
    }

    @Test
    fun ` register should throw AuthenticationException 3 `() {
        factory.newUser(UUID.randomUUID().toString(), createRandomEmail()).let { user ->
            UserRegistrationRequest(user.name, user.email, createPassword(), Credit.withDefaults(10L)).let { urr ->
                `when`(mockFactory.newUser(anyString(), anyString(), any())).thenReturn(user)
                doNothing().`when`(mockUserRepository).save(user)
                `when`(mockPublisher.publish(any(UserCreatedDomainEvent::class.java))).then { throw Exception() }

                assertThrows<AuthenticationException> { jusApiUserServices.register(urr) }
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
                    verify(mockUserRepository, never()).save(user)
                    verify(mockUserRepository, times(1)).save(userWithIncreasedBalance)
                }
        }
        val user = createUserWithCredit()
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
        val user = createUserWithCredit()
    }
}