package com.lifeleveling.graphics

class NativeLib {

    /**
     * A native method that is implemented by the 'graphics' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'graphics' library on application startup.
        init {
            System.loadLibrary("graphics")
        }
    }
}