package com.github.takahirom.roborazzi.usage.examples

/**
 * Previews annotated with [IgnoreScreenshotTest] will be ignored for automatic screenshot generation.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class IgnoreScreenshotTest(val reason: String = "")