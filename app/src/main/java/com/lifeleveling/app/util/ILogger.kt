package com.lifeleveling.app.util

/**
 * DO NOT DECLARE AN OBJECT OF THIS INTERFACE. use one of the derived classes AndroidLogger, or Test Logger
 * This is a logger Interface so we can use abstraction to modify the logging behavior of functions
 * depending on whether they are running in Instrumented, or Unit Tests.
 * @author thefool309
 * @see AndroidLogger
 * @see TestLogger
 * @see android.util.Log
 */
interface ILogger {
    /**
     * Send a DEBUG log message
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @returns Nothing
     * @see android.util.Log.d
     */
    fun d(tag: String, message: String)
    /**
     * Send an ERROR log message without a throwable
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.e
     */
    fun e(tag: String, message: String)
    /**
     * Send an ERROR log message with a throwable
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @param throwable The error thrown
     * @see android.util.Log.e
     */
    fun e(tag: String, message: String, throwable: Throwable)

    /**
     * Send a WARN log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.w
     */
    fun w(tag: String, message: String)

    /**
     * send an INFO log message
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.i
     */
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