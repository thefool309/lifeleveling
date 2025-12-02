package com.lifeleveling.app.util

/**
 * This is a logger Interface so we can use abstraction to modify the logging behavior of functions
 * depending on whether they are running in Instrumented, or Unit Tests.
 * I won't include any function definitions for these logger classes.
 * They are fairly self explanatory as they directly mimic the `Android.log` library.
 * @author thefool309
 * @see AndroidLogger
 * @see TestLogger
 * @see android.util.Log
 */
interface ILogger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable)
    fun w(tag: String, message: String)
    fun i(tag: String, message: String)

    companion object{
        val DEFAULT: ILogger = object : ILogger {
            override fun d(tag: String, message: String) {}
            override fun e(tag: String, message: String) {}
            override fun e(tag: String, message: String, throwable: Throwable) {}
            override fun w(tag: String, message: String) {}
            override fun i(tag: String, message: String) {}
        }
    }
}