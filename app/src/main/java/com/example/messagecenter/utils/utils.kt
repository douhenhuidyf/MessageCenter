package com.example.messagecenter.utils

import java.text.SimpleDateFormat

fun timestampToString(timestamp: Long): String {
    val nowTime = System.currentTimeMillis()
    val difference = nowTime - timestamp
    var timeString = ""
    val oneMinute = 60 * 1000
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour
    val yesterday = 2 * oneDay
    val sevenDays = 7 * oneDay
    when{
        difference < oneMinute -> timeString = "刚刚"
        difference < oneHour -> timeString = "${difference / (60 * 1000)}分钟前"
        difference < oneDay -> timeString = "${difference / (60 * 60 * 1000)}小时前"
        difference < yesterday -> timeString = "昨天"
        difference < sevenDays -> timeString = "${difference / oneDay}天前"
        else -> {
            val date = java.util.Date(timestamp)
            val dateFormat = SimpleDateFormat("YY-MM-dd")
            timeString = dateFormat.format(date)
        }
    }
    return timeString
}