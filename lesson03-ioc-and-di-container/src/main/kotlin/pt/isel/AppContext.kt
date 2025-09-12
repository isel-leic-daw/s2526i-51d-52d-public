package pt.isel

import jakarta.inject.Named
import java.io.File
import java.lang.IllegalStateException
import java.net.URL
import java.util.Enumeration
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

fun <T : Any> loadInstanceOf(klass: KClass<T>): T {
    val concreteClass =
        if (klass.isAbstract) {
            loadCompatibleClass(klass)
        } else {
            klass
        }
    val first = concreteClass.constructors.firstOrNull()
    requireNotNull(first) { "$klass is missing a constructor!" }
    val args: List<Any> =
        first.parameters.map { arg ->
            val argKlass = arg.type.classifier as KClass<*>
            loadInstanceOf(argKlass)
        }
    @Suppress("UNCHECKED_CAST")
    return first.call(*args.toTypedArray()) as T
}

fun <T : Any> loadCompatibleClass(klass: KClass<T>): KClass<*> =
    klassesInClasspath()
        .firstOrNull {
            it.isSubclassOf(klass) &&
                !it.isAbstract // &&
                // it.hasAnnotation<Named>()
        }
        ?: throw IllegalStateException("No available Named class compatible with $klass")

fun klassesInClasspath(): Sequence<KClass<*>> =
    sequence {
        val classLoader = ClassLoader.getSystemClassLoader()
        val resources: Enumeration<URL> = classLoader.getResources("") // Scans the root of the classpath
        while (resources.hasMoreElements()) {
            val url: URL = resources.nextElement()

            if (url.protocol == "file" && File(url.toURI()).isDirectory) {
                // Scan the directory for .class files
                yieldAll(
                    scanDirectory(File(url.toURI()), "").map {
                        classLoader.loadClass(it).kotlin
                    },
                )
            }
        }
    }

private fun scanDirectory(
    directory: File,
    packageName: String,
): Sequence<String> =
    sequence {
        directory
            .listFiles()
            ?.forEach { file ->
                if (file.isDirectory) {
                    yieldAll(scanDirectory(file, packageName + file.name + "."))
                } else if (file.name.endsWith(".class")) {
                    val className = packageName + file.name.replace(".class", "")
                    yield(className)
                }
            }
    }
