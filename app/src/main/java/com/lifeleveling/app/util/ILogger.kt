package com.lifeleveling.app.util

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