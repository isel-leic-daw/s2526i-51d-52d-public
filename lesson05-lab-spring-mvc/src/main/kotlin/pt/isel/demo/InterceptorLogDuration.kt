package pt.isel.demo

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.lang.System.nanoTime

const val START_TIME_KEY = "startTime"

@Component
class InterceptorLogDuration : HandlerInterceptor {
    companion object {
        private val logger = LoggerFactory.getLogger(InterceptorLogDuration::class.java)
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        request.setAttribute(START_TIME_KEY, nanoTime())
        return super.preHandle(request, response, handler)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        super.afterCompletion(request, response, handler, ex)
        val duration = request.getAttribute(START_TIME_KEY) as Long - nanoTime()
        logger.info("Request processing duration: ${duration / 1000} us")
    }
}
