package com.pujaad.coopmart.extension

import android.os.Build
import androidx.annotation.RequiresApi
import com.pujaad.coopmart.api.common.Constant
import java.text.SimpleDateFormat
import java.util.*

fun String?.toStringOrEmpty() : String {
    return this ?: ""
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.toByteArray() : ByteArray? {
    return try {
        Base64.getDecoder().decode(this)
    } catch (exception: Exception) {
        null
    }
}

fun String.toDoubleOrZero(): Double {
    return try {
        this.toDouble()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        0.0
    }
}

fun String.toAppDateFormat(): String {
    return try {
        val calendar = Calendar.getInstance()
        val serverDateFormatter = SimpleDateFormat(Constant.SERVER_DATE_FORMAT)
        val appDateFormatter = SimpleDateFormat(Constant.LOCAL_DATE_FORMAT)
        calendar.time = serverDateFormatter.parse(this)
        appDateFormatter.format(calendar.time)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun String.toReportBodyDateFormat(): String {
    return try {
        val calendar = Calendar.getInstance()
        val serverDateFormatter = SimpleDateFormat(Constant.APP_DATE_FORMAT)
        val appDateFormatter = SimpleDateFormat(Constant.REPORT_BODY_DATE_FORMAT)
        calendar.time = serverDateFormatter.parse(this)
        appDateFormatter.format(calendar.time)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}