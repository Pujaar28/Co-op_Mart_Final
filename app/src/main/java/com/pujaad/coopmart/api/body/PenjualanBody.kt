package com.pujaad.coopmart.api.body

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PenjualanBody(
    var id_karyawan : Int,
    var customer_name: String,
    var payment: Int,
    var items: List<PenjualanItemBody>,
) : Parcelable