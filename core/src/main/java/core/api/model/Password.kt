package core.api.model

data class Password(
    val value: String,
){
    companion object{
        fun empty() =
            Password(
                value = ""
            )
    }
}
