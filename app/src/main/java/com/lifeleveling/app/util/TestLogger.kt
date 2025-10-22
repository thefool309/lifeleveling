package com.lifeleveling.app.util

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