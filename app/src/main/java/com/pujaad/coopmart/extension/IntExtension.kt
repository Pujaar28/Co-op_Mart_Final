package com.pujaad.coopmart.extension

import java.text.NumberFormat
import java.util.Locale

fun toGermanFormat(number: Number?): String {
    return if (number == null) NumberFormat.getNumberInstance(Locale.GERMAN).format(0)
    else NumberFormat.getNumberInstance(Locale.GERMAN).format(number)
}

fun Int.toIDRFormat(): String = "Rp. ${toGermanFormat(this)}"