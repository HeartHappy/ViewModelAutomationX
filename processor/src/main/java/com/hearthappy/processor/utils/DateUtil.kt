package com.hearthappy.processor.utils

object DateUtil {

    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val remainingMilliseconds = milliseconds % 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return when {
            minutes > 0          -> "%d m %d s %d ms".format(minutes, remainingSeconds, remainingMilliseconds)
            remainingSeconds > 0 -> "%d s %d ms".format(remainingSeconds, remainingMilliseconds)
            else                 -> "%d ms".format(remainingMilliseconds)
        }
    }
}
