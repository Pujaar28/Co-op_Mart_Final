package com.pujaad.coopmart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductCategory(
    var id: Int,
    var name : String,
    var description: String,
) : Parcelable