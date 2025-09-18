# Spring Boot introductory exercises

Add a **new branch** to the GitHub DAW local repository and check it out. Create
a file named `lab01-answers.md` in the `lesson04-lab-spring-mvc` folder and
write your answers to the following questions there.

## A - Spring Setup

1. Go to [https://start.spring.io/](https://start.spring.io/) and select:
    - Gradle project (the same build system we used in the LS course), using the
      Kotlin language for the build script.  
      - Q: what is a build script?
    - Kotlin language (or Java in alternative)
    - The latest stable version of Spring Boot
    - JAR packaging
    - Java version 21
    - Define the project metadata, such as group, artifact, …
    - Also select the following dependencies:
      - `Spring Web` (NOT `Spring Reactive Web`)
2. Finally, select **Generate**. This will produce a compressed archive.
3. Uncompress the archive file into a folder.
4. Inspect the folder, noticing:
    - The presence of a `gradlew` file indicates that this is a Gradle-based
      project.
    - The presence of a `build.gradle.kts` containing:
      - The plugins, namely: `kotlin` and others specific to Spring (these will
        add new build tasks).
      - The required dependencies:
        - The Spring Boot library —
          `org.springframework.boot:spring-boot-starter-web`
        - The [Jackson library](https://github.com/FasterXML/jackson), which
          handles JSON serialization and deserialization
        - The Kotlin reflection library
        - The Spring Boot library for tests —
          `org.springframework.boot:spring-boot-starter-test`  
        - Q: what is the difference between `implementation` and
          `testImplementation` in the `dependencies` block?
5. Copy the `src` folder and `build.gradle.kts` file from the Spring-generated
   project into the `lesson04-lab-spring-mvc` folder of local DAW repository.
   Remember to checkout in a different branch from main.
6. Include the `lesson04-lab-spring-mvc` module in the root `settings.gradle.kts`.
7. Start IntelliJ for DAW repository project and synchronizes with the new
   gradle project definition.
    - There is a single file under the `main` folder — `DemoApplication.kt` (or
      similar).
      - It has an empty class `DemoApplication` (or similar), annotated with
        `@SpringBootApplication`.
      - It also has a main function that simply calls
        `runApplication<DemoApplication>(*args)`, using the above class as the
        generic argument.
    - Notice how the project defines a rather simple console application with a
      `main` entry point.

## B - Building and running the application

1. Run `./gradlew build`
    - There will be at least one JAR inside `build/libs`.
    - One of those JARs contains all the classes required to run the
      application, including the third-party dependencies, such as the Spring
      libraries. No other dependency will be needed for that.
      - Uncompress the JAR and take a look around. Notice `.class` files
        originating from the project’s source code and from the dependencies.
      - It is usual to name this JAR as the *uber JAR* or *fat JAR*.
2. Run the application with `java -jar build/libs/<library-name>.jar`.
    - Notice the following in the output:  
      `Tomcat started on port(s): 8080 (http) with context path ''`
      - Tomcat is a servlet server, similar to Jetty (which we used in LS).
      - The server will use port 8080.
      - The `context path` is `''`, meaning all application paths start from the
        root.
3. To stop the application, press `Control-C`.
    - Notice the shutdown process in the message:  
      `Shutting down ExecutorService 'applicationTaskExecutor'`.

## C - Adding HTTP request handlers

1. The project is configured with a library called **Spring MVC** to handle HTTP
   requests.
   - MVC comes from *Model-View-Controller*.
2. A way to define HTTP request handlers is by defining methods inside a
   *controller* class.
   - A controller is a class that contains request-handling methods
     (*handlers*).
3. Create an `ExampleController` class:
    - Annotate it with `@RestController`.
      - This marks the class as a controller for Spring.
      - Notice there isn’t a required base class or implemented interface.
      - The meaning of *Rest* in `RestController` will be discussed later.
    - Inside this class, create a method that returns the `Hello Web` string and
      annotate it with `@GetMapping("/examples/1")`.
      - This annotation defines the mapping between the request’s HTTP
        method/URI path and the handler method.

```kotlin
@GetMapping("/examples/1")
fun get() = "Hello Web"
````

4. Start the application and do a request to [http://localhost:8080/examples/1](http://localhost:8080/examples/1).

    * Notice that the response contains the `Hello Web` string in the body.
    * Do the request using curl:
      `curl -i http://localhost:8080/examples/1`
      and notice the `Content-Type` header is `text/plain;charset=UTF-8`.
    * Do the request from a browser and notice the `Content-Type` is
      `text/html;charset=UTF-8`.
       * Q: Why is the response’s content type different? Can you change the
         curl command so the response also uses the `text/html` media type?
    * Break the execution inside a handler function and observe the call stack.

## D - Controller life-cycle

1. By using logging statements and performing multiple HTTP requests:
    * Observe how many `ExampleController` instances are created.
    * Observe the identifiers of the threads where the handler methods are
      called.
2. Q: Given the above observations, what should be the restrictions on the
   instance state?

## E - Dependencies, inversion of control, and dependency injection

1. Start by creating an interface that defines the functionality of a *service*,
   in this case a service responsible for computing a greeting message:

```kotlin
interface GreetingsService {
    val greeting: String
}
```

2. Then, create a simple implementation of that service returning a hard-coded message. This class must be annotated with `@Component` or `@Service`:

```kotlin
@Component
class DefaultGreetingService : GreetingsService {
    override val greeting: String = "Hello DAW"
}
```

3. Finally, have the `ExampleController` receive a `GreetingsService` instance via the constructor and use it in the handler method:

```kotlin
@RestController
class ExampleController(
    private val greetingsService: GreetingsService,
) {

    @GetMapping("/examples/1")
    fun getHello() = greetingsService.greeting
}
```

4. Restart the application, do a `GET` request to [http://localhost:8080/examples/1](http://localhost:8080/examples/1), and observe the result.
5 Key concepts:
    * **Dependency**: `ExampleController` depends of the `GreetingsService`.
    * **Inversion of Control**: the `ExampleController` does not instantiate the
      dependency. It *receives* it via the constructor.
      * This decouples the controller from the implementation of the service.
      * `ExampleController` depends only on the interface, not on the
        implementation.
    * **Injection**: the dependency is *injected* into the instance.
      * Since it’s provided via the constructor, this is called *constructor
        injection*.
      * Constructor injection ensures that an instance is always created in a
        valid state (with required dependencies).
    * **Dependency Graph**:
      * The graph vertices are the instances (`ExampleController`,
        `GreetingsService`).
      * The graph edges are the dependency relations (`ExampleController` →
        `GreetingsService`).
      * Real-world applications have much more complex dependency graphs.
      * The creation of the dependency graph is called *composition*.

## F - Container or context

1. Where and when are `ExampleController` and `DefaultGreetingService` instances
   created?
   * Until now, we just defined classes and constructors. No instance creation
     is in the application code.
2. The instantiation is performed by a *dependency injection container*.
3. In Spring, the container is called a **context**, and the managed instances
   are called **beans**.
4. Q: How does the context determine dependency relations?
5. Q: How does the context determine which classes to instantiate?

## G - `HttpServletRequest` and `HttpServletResponse`

  1. Support showing the greetings message in multiple languages. Use the
     `Accept-Language` request header to help make the decision of which
     language to use. Use the `Content-Language` header, placed in the
     response, to inform the client of which language was used.
     - Add parameters of type `HttpServletRequest` and `HttpServletResponse` to
       manage the request and response headers in the `hello` handler.

## H - Interceptor

- Add to the log message both the name of the controller and the name of the
  handler function selected to process the request, if any.
    - Use the
      [HandlerInterceptor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html)
      extensibility point to obtain information about the used controller and
      handler.
        - To register the interceptor, make the config class implement the
          `WebMvcConfigurer` interface and use the `addInterceptors` method from
          this interface.
        - You can obtain the controller type and handler method reflectively
          from the `handler` parameter of `preHandle` using:
        ```kotlin
        if (handler is HandlerMethod) {
          val controllerType = handler.beanType
          val method = handler.method
        }
        ```
