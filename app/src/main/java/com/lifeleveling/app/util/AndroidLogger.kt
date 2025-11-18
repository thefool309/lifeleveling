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
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
}