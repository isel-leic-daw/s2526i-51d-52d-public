package pt.isel

const val TOP_100_MOVIES =
    "https://gist.githubusercontent.com/fmcarvalho/" +
        "6d966b2d97d7b268102efa56dc00692c/raw/ffb6ebff59a1862eedf6b9856b0c92a7573d4cda/top_100_movies.csv"

class MovieLister {
    lateinit var finder: MovieFinder

    /**
     * IoC => Inversion of Control:
     * * This class does NOT instantiate its dependencies
     * * The client is responsible for instantiating the dependency
     */

    fun moviesDirectedBy(arg: String): Sequence<Movie> {
        check(finder != null) { "MovieLister requires an instance of MovieFinder!" }
        return finder
            .findAll(TOP_100_MOVIES)
            .filter { it.director.lowercase().contains(arg.lowercase()) }
    }
}
