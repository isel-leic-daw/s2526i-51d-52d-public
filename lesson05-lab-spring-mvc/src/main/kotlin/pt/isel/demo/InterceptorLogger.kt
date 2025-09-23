package pt.isel.demo

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

const val HANDLER_INFO_KEY = "handlerInfo"

@Component
class InterceptorLogger : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(InterceptorLogger::class.java)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (handler is HandlerMethod) {
            val controllerName = handler.beanType.simpleName
            val methodName = handler.method.name

            val url = request.requestURL
            // logger.info("Handling request to $url with controller=$controllerName, method=$methodName")
            request.setAttribute(HANDLER_INFO_KEY, "controller=$controllerName method=$methodName")
        }
        return super.preHandle(request, response, handler)
    }
}
