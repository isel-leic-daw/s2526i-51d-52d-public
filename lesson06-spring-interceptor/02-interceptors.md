# Interceptors

- Create and configure an **interceptor** that emits a log message for every processed
  HTTP request.
    - The log message must include: the HTTP request's method and URI; the
      response's status code; and an approximation of the time it took to
      process the request.
    - Use the
      [HandlerInterceptor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html)
      extensibility.
        - To register the interceptor, make the config class implement the
          `WebMvcConfigurer` interface and use the `addInterceptors` method from
          this interface.
        - Use the `setAttribute` and `getAttribute` methods from
          `HttpServletRequest` as a way of communicating information between the
          `preHandle` and the `afterCompletion` methods.
    - You may extend the existing example of [InterceptorLogger](https://github.com/isel-leic-daw/s2526i-51d-52d-public/blob/main/lesson05-lab-spring-mvc/src/main/kotlin/pt/isel/demo/InterceptorLogger.kt)
