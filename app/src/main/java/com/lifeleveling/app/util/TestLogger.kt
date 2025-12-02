package com.lifeleveling.app.util

/**
 * A logger type created for use with Unit Tests or anything that isn't directly connected to the
 * `android` library. This uses println() instead of the typical `android.util.Log` calls, so that it can be executed in the JVM
 * @author thefool309
 * @see ILogger
 * @see AndroidLogger
 * @see android.util.Log
 */
class TestLogger : ILogger{
    override fun d(tag: String, message: String) {
        println("$tag: $message")
    }

    override fun e(tag: String, message: String) {
        println("$tag: $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        println("$tag: $message : ${throwable.message}")
        throwable.printStackTrace()
    }

    override fun w(tag: String, message: String) {
        println("$tag: $message")
    }

    override fun i(tag: String, message: String) {
        println("$tag: $message")
    }
}