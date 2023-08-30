package com.pujaad.coopmart.api.body

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PenjualanItemBody(
    val id_product: Int?,
    val quantity: Int,
    val price: Int,
) : Parcelable