package authentication.api

import authentication.app.Factory
import authentication.app.UserCreatedDomainEvent
import authentication.domain.EventPublisher
import authentication.domain.model.AccountType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

class ComponentTest {

    private val eventPublisher: EventPublisher = mock(EventPublisher::class.java)
    private val factory = Factory(eventPublisher)

    @Test
    fun ` todo novo usuario tem uma conta associada `() {
        assertTrue(createUser().hasFunctionalAccount())
    }

    @Test
    fun ` todo novo usuario tem dados cadastrais `() {
        assertNotNull(createUser().email)
        assertNotNull(createUser().name)
    }

    @Test
    fun ` todo novo usuario tem uma conta STANDARD `() {
        assertEquals(AccountType.STANDARD, createUser().accountType())
    }

    @Test
    fun ` todo novo usuario esta nem bloqueado nem deletado `() {
        assertEquals(false, createUser().accountIsBlocked())
        assertEquals(false, createUser().accountIsDeleted())
    }

    @Test
    fun ` quando deletar usuario tem que bloquear e deletar a conta tambem `() {
        val user = createUser().delete()
        assertEquals(true, user.isDeleted)
        assertEquals(true, user.accountIsDeleted())
        assertEquals(true, user.accountIsBlocked())
    }

    @Test
    fun ` quando deletar a conta tem que bloquea-la tambem sem bloquear o usuario `() {
        val user = createUser().deleteCurrentAccount()
        assertEquals(false, user.isDeleted)
        assertEquals(true, user.accountIsDeleted())
        assertEquals(true, user.accountIsBlocked())
    }

    @Test
    fun ` todo novo usuario tem uso igual a zero `() {
        assertEquals(0, createUser().usageQuantity())
    }

    @Test
    fun ` quando usuario faz novo acesso o uso incrementa `() {
        createUser().let { user ->
            val initial = user.usageQuantity()
            val terminal = user
                .incrementAccessCount()
                .incrementAccessCount()
                .incrementAccessCount()
                .usageQuantity()
            assertEquals(initial + 3, terminal)
        }
    }

    @Test
    fun ` Quando usuario eh criado um evento eh disparado `() {
        factory.newUser(
            name = UUID.randomUUID().toString(),
            email = UUID.randomUUID().toString()
        ).let { user ->
            verify(eventPublisher, times(1))
                .publish(UserCreatedDomainEvent(user))
        }
    }

    private fun createUser() = factory.newUser(
        name = UUID.randomUUID().toString(),
        email = UUID.randomUUID().toString()
    )

}