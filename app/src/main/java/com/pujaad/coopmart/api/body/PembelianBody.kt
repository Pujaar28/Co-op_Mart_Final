package com.pujaad.coopmart.api.body

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PembelianBody(
    var id_karyawan : Int,
    var items: List<PembelianItemBody>,
) : Parcelable