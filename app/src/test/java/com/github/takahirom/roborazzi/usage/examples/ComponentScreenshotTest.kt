package com.github.takahirom.roborazzi.usage.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.roboOutputName
import io.github.classgraph.ClassGraph
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * Based on https://github.com/rkam88/nowinandroid implementation
 */
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
class ComponentScreenshotTest(
    private val composablePreview: ComposablePreviewFunction,
) {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun test() {
        composeRule.setContent {
            composablePreview()
        }

        composeRule.onRoot().captureRoboImage(
            roboOutputName() + "_" + composablePreview.toString() + ".png"
        )
    }

    /**
     * Provides a list of [ComposablePreviewFunction]s to be used as test parameters.
     */
    companion object {

        private const val APPLICATION_PACKAGE = "com.github.takahirom.roborazzi.usage.examples"

        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun provideValues(): Iterable<Array<Any>> {
            val composablePreview = mutableListOf<ComposablePreviewFunction>()

            findPreviewMethodsInCodebase()
                .filterNot { method -> method.annotations.any { it is IgnoreScreenshotTest } }
                .onEach { if (Modifier.isPrivate(it.modifiers)) it.isAccessible = true }
                .forEach { method ->
                    val providerAnnotation = method.findPreviewParameterAnnotation()

                    if (providerAnnotation == null) {
                        // The [Preview] doesn't have a parameter annotated with [PreviewParameter],
                        // create a [ComposablePreviewFunction] with no parameters.
                        composablePreview.add(
                            method.toComposablePreview(),
                        )
                    } else {
                        // Create an instance of the PreviewParameterProvider.
                        val provider = providerAnnotation.provider.createInstanceUnsafe()

                        // Get a sequence with the name and value for each parameter
                        // that the [Preview] should be called with.
                        val nameToValue =
                            provider.values.mapIndexed { index, value ->
                                index.toString() to value
                            }

                        // Create a [ComposablePreviewFunction] for each name and value pair.
                        nameToValue.forEach { (nameSuffix, value) ->
                            composablePreview.add(
                                method.toComposablePreview(
                                    nameSuffix,
                                    value
                                )
                            )
                        }
                    }
                }

            return composablePreview.map { arrayOf(it) }
        }

        /**
         * Finds all [Preview] methods in the codebase.
         *
         * Additionally finds methods annotated with the [ThemePreviews] and [DevicePreviews]
         * multi-preview annotations specific to Now in Android.
         */
        private fun findPreviewMethodsInCodebase(): List<Method> {
            val scanResult = ClassGraph()
//                .verbose()               // Log to stderr
                .overrideClassLoaders(Thread.currentThread().contextClassLoader)
                .enableAllInfo()
                .acceptPackages(APPLICATION_PACKAGE)     // Scan com.xyz and subpackages (omit to scan all packages)
                .scan()
            return scanResult.getClassesWithMethodAnnotation(Preview::class.java.canonicalName)
                .flatMap {
                    it.methodInfo.filter { it.hasAnnotation(Preview::class.java) }
                        .map { it.loadClassAndGetMethod() }
                }
        }

        /**
         * Finds the [PreviewParameter] annotation on the method's parameters.
         */
        private fun Method.findPreviewParameterAnnotation(): PreviewParameter? {
            return this.parameterAnnotations
                .flatMap { it.toList() }
                .find { it is PreviewParameter } as PreviewParameter?
        }

        /**
         * Creates an instance of the [PreviewParameterProvider] using the no-args constructor
         * even if the class or constructor is private.
         */
        private fun KClass<out PreviewParameterProvider<*>>.createInstanceUnsafe(): PreviewParameterProvider<*> {
            val noArgsConstructor: KFunction<PreviewParameterProvider<*>> = constructors
                .single { it.parameters.all(KParameter::isOptional) }
//            noArgsConstructor.isAccessible = true
            return noArgsConstructor.call()
        }
    }
}

interface ComposablePreviewFunction {

    @Composable
    operator fun invoke()
}

private const val NAME_SEPARATOR = "_"

/**
 * Creates a [ComposablePreviewFunction] from a [Method].
 * Because the method is a [Composable] function that has extra parameters added by the compiler
 * ([Composer] being one of them), we can't call it directly. Instead, we rely on
 * Java dynamic proxy to handle the call using a [ComposablePreviewInvocationHandler] instance.
 */
private fun Method.toComposablePreview(
    nameSuffix: String = "",
    parameter: Any? = ComposablePreviewInvocationHandler.NoParameter,
): ComposablePreviewFunction {
    val proxy = Proxy.newProxyInstance(
        ComposablePreviewFunction::class.java.classLoader,
        arrayOf(ComposablePreviewFunction::class.java),
        ComposablePreviewInvocationHandler(composableMethod = this, parameter = parameter),
    ) as ComposablePreviewFunction

    // Wrap the call to the proxy in an object so that we can override the toString method
    // to provide a more descriptive name for the test and resulting snapshot filename.
    return object : ComposablePreviewFunction by proxy {
        override fun toString(): String {
            return buildList<String> {
                add(declaringClass.simpleName)
                add(name)
                if (nameSuffix.isNotEmpty()) add(nameSuffix)
            }.joinToString(NAME_SEPARATOR)
        }
    }
}


/**
 * Used to handle calls to a [composableMethod].
 * If a [parameter] is provided, it will be used as the first parameter of the call.
 */
private class ComposablePreviewInvocationHandler(
    private val composableMethod: Method,
    private val parameter: Any?,
) : InvocationHandler {

    /**
     * Used to indicate that no parameter should be used when calling the [composableMethod].
     * We can't use null here as we might want to pass null as an actual parameter to a function.
     */
    object NoParameter

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        val safeArgs = args ?: emptyArray()
        val safeArgsWithParam = if (parameter != NoParameter) {
            arrayOf(parameter, *safeArgs)
        } else {
            safeArgs
        }
        composableMethod.isAccessible = true
        return composableMethod.invoke(null, *safeArgsWithParam)
    }
}