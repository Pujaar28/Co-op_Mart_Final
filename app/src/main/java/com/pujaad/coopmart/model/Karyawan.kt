package com.pujaad.coopmart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Karyawan(
    var id: Int,
    var username : String,
    var name : String,
    var type: Int,
    var age: Int?,
    var phone: String,
    var address: String,
) : Parcelable