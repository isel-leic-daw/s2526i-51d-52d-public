package pt.isel.http

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerArgParser {
    /**
     * Example of a route with query-string parameters
     * Try with:
     *     curl 'http://localhost:8080/path0?nr=17'
     * Argument parser is responsible for converting the String to Int.
     * NOTE: use Nullable for optional qs parameters
     */
    @GetMapping("/path0")
    fun handler0QsParser(
        @RequestParam nr: Int?,
    ): String = "Request to path 0 with argument: $nr"

    /**
     * Example of a route with path parameter
     * Try with:
     *     curl 'http://localhost:8080/path1/17'
     * Argument parser is responsible for converting the String to Int.
     */
    @GetMapping("/path1/{nr}")
    fun handler1RoutePathParamParser(
        @PathVariable nr: Int,
    ): String = "Request to path 1 with path parameter: $nr"
}
