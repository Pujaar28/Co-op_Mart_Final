package com.pujaad.coopmart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonthModel(
    var id: Int = 0,
    var user_id: Int = 0,
    var date: String = "",
    var name: String = ""
) : Parcelable
