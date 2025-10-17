package com.lifeleveling.app.util

interface ILogger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable)
    fun w(tag: String, message: String)
    fun i(tag: String, message: String)
}