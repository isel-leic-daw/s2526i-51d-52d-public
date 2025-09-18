package pt.isel.demo

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("terminator")
class ServiceGreetingsDefault : ServiceGreetings {
    override fun greetings(lang: Language): GreetingsMessage =
        GreetingsMessage(
            "I'll be back.",
            Language.EN,
        )
}
