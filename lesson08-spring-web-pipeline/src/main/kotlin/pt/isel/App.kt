package pt.isel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class App

@Configuration
class AppConfig(
    //val convUriToQrCode: MsgConverterUrlToQrCode,
    val clientIpArgumentResolver: ClientIpArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        super.addArgumentResolvers(resolvers)
        resolvers.add(clientIpArgumentResolver)
    }
}

fun main() {
    runApplication<App>()
}
