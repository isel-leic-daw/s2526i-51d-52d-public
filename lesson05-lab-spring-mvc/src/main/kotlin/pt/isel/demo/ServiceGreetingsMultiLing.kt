package pt.isel.demo

import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import pt.isel.demo.Language.EN
import pt.isel.demo.Language.PT

@Component
@Primary
@Profile("multi-ling")
class ServiceGreetingsMultiLing : ServiceGreetings {
    override fun greetings(lang: Language): GreetingsMessage =
        when (lang) {
            PT -> GreetingsMessage("Ola do ISEL", PT)
            EN -> GreetingsMessage("Hello from ISEL", EN)
        }
}
