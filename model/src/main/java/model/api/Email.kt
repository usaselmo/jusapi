package model.api

import java.lang.RuntimeException

data class Email(
    val value: String,
){
    init {
        if (!value.matches(Regex("^(.+)@(\\S+)$"))) {
            throw RuntimeException("Email not valid: $value")
        }
    }
}
