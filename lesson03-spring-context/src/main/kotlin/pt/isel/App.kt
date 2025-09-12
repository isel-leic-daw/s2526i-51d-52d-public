package pt.isel

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Profile("viaFile")
class AppConfigViaFile {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun createBeanDataSourceClient() = DataSourceClientViaFile()
}

@Component
@Profile("viaWeb")
class AppConfigViaWeb {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun createBeanDataSourceClient() = DataSourceClientViaUrl()
}

fun main() {
    val context =
        AnnotationConfigApplicationContext().also { ctx ->
            ctx.environment.setActiveProfiles("viaWeb")
            ctx.scan("pt.isel")
//            ctx.registerBean(
//                DataSourceClientViaFile::class.java,
//                BeanDefinitionCustomizer { it.scope = BeanDefinition.SCOPE_PROTOTYPE },
//            )
//            ctx.registerBean(
//                MovieLister::class.java,
//                BeanDefinitionCustomizer { it.scope = BeanDefinition.SCOPE_PROTOTYPE },
//            )
            ctx.refresh()
        }
    println(context.getBean(DataSourceClient::class.java))
    println(context.getBean(DataSourceClient::class.java))
    val lister = context.getBean(MovieLister::class.java)
    println(lister)
    println(context.getBean(MovieLister::class.java))

    lister
        .moviesDirectedBy("tarantino")
        .forEach { println(it) }
}
