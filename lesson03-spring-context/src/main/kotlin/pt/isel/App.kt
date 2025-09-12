package pt.isel

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main() {
    // context is the IoC DI Container
    val context =
        AnnotationConfigApplicationContext().also { ctx ->
            ctx.registerBean(
                DataSourceClientViaUrl::class.java,
                BeanDefinitionCustomizer { it.scope = BeanDefinition.SCOPE_SINGLETON },
            )
            ctx.scan("pt.isel")
            ctx.registerBean(
                MovieLister::class.java,
                BeanDefinitionCustomizer { it.scope = BeanDefinition.SCOPE_PROTOTYPE },
            )
            ctx.refresh()
        }
    val lister = context.getBean(MovieLister::class.java)
    println(lister)
    println(context.getBean(MovieLister::class.java))

    lister
        .moviesDirectedBy("scorsese")
        .take(3)
        .forEach { println(it) }
}
