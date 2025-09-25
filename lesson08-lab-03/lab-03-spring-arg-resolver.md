# Lab 03 - Spring Arguments Binding

Following item G of the Lesson 4 lab, create a class implementing
the `HandlerMethodArgumentResolver` interface as a way of providing
the `Accept-Language` in a strongly-typed way:
* Use the `WebMvcConfigurer` implementation to register the `HandlerMethodArgumentResolver`.
* Take advantage of the information provided by the HandlerMethodArgumentResolver in the greetings controller.
