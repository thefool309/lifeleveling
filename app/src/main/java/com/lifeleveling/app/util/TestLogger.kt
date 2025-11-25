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
    /**
     * Send a DEBUG log message to the Java Runtime Machine (for use in non instrumented unit tests)
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @returns Nothing
     * @see android.util.Log.d
     */
    override fun d(tag: String, message: String) {
        println("$tag: $message")
    }
    /**
     * Send an ERROR log message without a throwable
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.e
     */
    override fun e(tag: String, message: String) {
        println("$tag: $message")
    }
    /**
     * Send an ERROR log message with a throwable
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @param throwable The error thrown
     * @see android.util.Log.e
     */
    override fun e(tag: String, message: String, throwable: Throwable) {
        println("$tag: $message : ${throwable.message}")
        throwable.printStackTrace()
    }
    /**
     * Send a WARN log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.w
     */
    override fun w(tag: String, message: String) {
        println("$tag: $message")
    }
    /**
     * send an INFO log message to the Jave Runtime Machine (for use in non instrumented unit tests)
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.i
     */
    override fun i(tag: String, message: String) {
        println("$tag: $message")
    }
}