package pt.isel.demo

data class GreetingsMessage(
    val message: String,
    val lang: Language = Language.EN,
)
