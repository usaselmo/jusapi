package ui.impl.webapi

import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ui.domain.UIException
import java.time.LocalDateTime

@ControllerAdvice
class UiControllerAdvice() {

    companion object {
        val log: Log = LogFactory.getLog(javaClass)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, httpServletRequest: HttpServletRequest): ResponseEntity<Error> {
        log.error(ex.message)
        return when (ex) {
            is UIException -> ex.message ?: "Erro interno"
            else -> "Erro interno"
        }.let { error ->
            ResponseEntity.internalServerError().body(
                Error(
                    errors = setOf(error),
                    timestamp = LocalDateTime.now(),
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                    path = httpServletRequest.contextPath
                )
            )
        }
    }
}

data class Error(
    val errors: Set<String>,
    val timestamp: LocalDateTime,
    val httpStatus: HttpStatus,
    val path: String
)