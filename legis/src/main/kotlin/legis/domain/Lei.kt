package legis.domain

import java.time.ZonedDateTime

data class Lei(
    val promulgacaoData: ZonedDateTime,
    val revogada: Boolean,
)
