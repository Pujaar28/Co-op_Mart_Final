package com.pujaad.coopmart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Supplier(
    var id: Int,
    var name : String,
    var code: String,
    var phone: String,
    var address: String,
    var description: String,
) : Parcelable