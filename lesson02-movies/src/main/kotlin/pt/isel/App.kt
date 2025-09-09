package pt.isel

fun main() {
    // resolveDependenciesManuallyByPropertyInjection()
    resolveDependenciesManuallyByConstructorInjection()
}

fun resolveDependenciesManuallyByPropertyInjection() {
//    val lister = MovieLister()
//    val finder = MovieFinderCsv()
//    lister.finder = finder
//    finder.client = DataSourceClientViaUrl()
//    lister
//        .moviesDirectedBy("nolan")
//        .take(5)
//        .forEach { println(it) }
}

fun resolveDependenciesManuallyByConstructorInjection() {
    val lister = MovieLister(MovieFinderCsv(DataSourceClientViaUrl()))
    lister
        .moviesDirectedBy("nolan")
        .take(5)
        .forEach { println(it) }
}
