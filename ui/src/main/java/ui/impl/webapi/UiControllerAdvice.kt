package ui.impl.webapi

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class UiControllerAdvice() {

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, httpServletRequest: HttpServletRequest): ResponseEntity<Set<Error>> {
        return ResponseEntity.internalServerError().body(
            setOf(
                Error(
                    ex.message ?: "Erro interno",
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    httpServletRequest.contextPath
                )
            )
        )
    }
}

data class Error(
    val error: String,
    val timestamp: LocalDateTime,
    val httpStatus: HttpStatus,
    val path: String
)