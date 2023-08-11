package model.api

data class Name(
    val fullName: String,
    val loginName: String
) {
    constructor(fullName: String) : this(fullName, fullName)
}