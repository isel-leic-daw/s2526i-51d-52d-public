package pt.isel.demo

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerHello(
    val serviceGreetings: ServiceGreetings,
) {
    @GetMapping("/hello")
    fun handlerGreetings(
        req: HttpServletRequest,
        // res: HttpServletResponse,
    ): Any {
        val lang =
            req
                .getHeader("Accept-Language")
                ?.split(',')
                ?.first()
                ?.let { Language.entries.firstOrNull { l -> l.langtag == it } } ?: Language.PT

//        res.setHeader("Content-Language", lang.langtag)
//        return serviceGreetings.greetings(lang)

        val greetings = serviceGreetings.greetings(lang)
        return ResponseEntity
            .ok() // status code 200
            .header("Content-Language", greetings.lang.langtag)
            .body(greetings)
    }
}
