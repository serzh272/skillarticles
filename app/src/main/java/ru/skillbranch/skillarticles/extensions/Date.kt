package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = date.time - this.time;
    val seconds = (diff / 1000)
    val minutes = (seconds / 60)
    val hours = (minutes / 60)
    val days = (hours / 24)

    return when (diff) {
        in 0L..1 * SECOND -> "just now"
        in 2 * SECOND until 45 * SECOND -> "a few seconds ago"
        in 45 * SECOND until 75 * SECOND -> "a minute ago"
        in 75 * SECOND until 45 * MINUTE -> "$minutes minutes ago"
        in 45 * MINUTE until 75 * MINUTE -> "hour ago"
        in 75 * MINUTE until 22 * HOUR -> "$hours hour ago"
        in 22 * HOUR until 26 * HOUR -> "one day ago"
        in 26 * HOUR until 360 * DAY -> "$days days ago"
        else -> "just now"
    }
}

fun Date.add(value: Long, units: TimeUnit = TimeUnit.SECONDS): Date {
    var time = this.time;

    time += when (units) {
        TimeUnit.SECONDS -> TimeUnit.SECONDS.toMillis(value)
        TimeUnit.MINUTES -> TimeUnit.MINUTES.toMillis(value)
        TimeUnit.HOURS -> TimeUnit.HOURS.toMillis(value)
        TimeUnit.DAYS -> TimeUnit.DAYS.toMillis(value)
        TimeUnit.NANOSECONDS -> TimeUnit.NANOSECONDS.toMillis(value)
        TimeUnit.MICROSECONDS -> TimeUnit.MICROSECONDS.toMillis(value)
        TimeUnit.MILLISECONDS -> value
    }
    this.time = time
    return this
}