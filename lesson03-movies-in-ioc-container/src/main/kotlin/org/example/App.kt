package org.example

import pt.isel.MovieLister
import pt.isel.loadInstanceOf

fun main() {
    val lister = loadInstanceOf(MovieLister::class)
    println(lister)
    println(loadInstanceOf(MovieLister::class))
    println(loadInstanceOf(MovieLister::class))

    lister
        .moviesDirectedBy("tarantino")
        .forEach { println(it) }
}
