package pt.isel.demo

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

const val START_TIME_KEY = "startTime"

@Component
class InterceptorLoggerDuration : HandlerInterceptor {
    companion object {
        private val logger = LoggerFactory.getLogger(InterceptorLoggerDuration::class.java)
    }

    // DON'T DO This => Shared Mutable State
    // var startTime: Long = 0

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val start = System.nanoTime()
        request.setAttribute(START_TIME_KEY, start)
        return super.preHandle(request, response, handler)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val start = request.getAttribute(START_TIME_KEY) as Long
        val duration = (System.nanoTime() - start) / 1000
        logger.info("Request processing duration $duration us")
        super.afterCompletion(request, response, handler, ex)
    }
}
