package pt.isel.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class DemoApplication

@Configuration
class DemoConfig(
    private val intLogger: InterceptorLogger,
    private val durLogger: InterceptorLogDuration
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        registry.addInterceptor(intLogger)
        // registry.addInterceptor(durLogger)
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
