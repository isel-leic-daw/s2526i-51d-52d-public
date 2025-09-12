package pt.isel

import jakarta.inject.Named
import java.net.URI


class DataSourceClientViaUrl : DataSourceClient {
    override fun load(path: String): Sequence<String> {
        return URI(path).toURL().openStream().bufferedReader().lineSequence()
    }
}
