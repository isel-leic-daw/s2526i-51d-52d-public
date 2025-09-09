package pt.isel

fun main() {
    resolveDependenciesManuallyByPropertyInjection()
}

fun resolveDependenciesManuallyByPropertyInjection() {
    val lister = MovieLister()
    val finder = MovieFinderCsv()
    lister.finder = finder
    finder.client = DataSourceClientViaUrl()
    lister
        .moviesDirectedBy("nolan")
        .take(5)
        .forEach { println(it) }
}
