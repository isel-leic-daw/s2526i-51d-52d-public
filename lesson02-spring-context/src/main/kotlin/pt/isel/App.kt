package pt.isel

import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main() {
    val context =
        AnnotationConfigApplicationContext(
            MovieLister::class.java,
            MovieFinderCsv::class.java,
            DataSourceClientViaFile::class.java,
        )
    val lister = context.getBean(MovieLister::class.java)
    println(lister)

    lister
        .moviesDirectedBy("tarantino")
        .forEach { println(it) }
}
