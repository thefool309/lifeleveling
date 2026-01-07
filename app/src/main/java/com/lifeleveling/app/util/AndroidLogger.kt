package com.lifeleveling.app.util

import android.util.Log

/**
 * This is a logger class based on the [ILogger] interface.
 * This is specifically making use of the `Android.log` library, and was built for production use and using with Integrated/Instrumented tests.
 * @author thefool309
 * @see ILogger
 * @see TestLogger
 * @see android.util.Log
 */
class AndroidLogger : ILogger {
    /**
     * Send a DEBUG log message
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @returns Nothing
     * @see android.util.Log.d
     */
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    /**
     * Send an ERROR log message with a throwable
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @param throwable The error thrown
     * @see android.util.Log.e
     */
    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
    /**
     * Send an ERROR log message with a throwable
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @param throwable The error thrown
     * @see android.util.Log.e
     */
    override fun e(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }
    /**
     * Send a WARN log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.w
     */
    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
    /**
     * send an INFO log message
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged
     * @see android.util.Log.i
     */
    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
}