package com.pujaad.coopmart.extension

import android.widget.DatePicker
import com.pujaad.coopmart.api.common.Constant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun DatePicker.getDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    return calendar.time
}

fun DatePicker.getDateInStr(): String {
    return try {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val date = Date(calendar.time.time)
        val format = SimpleDateFormat(Constant.APP_DATE_FORMAT)
        format.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}