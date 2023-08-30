package com.pujaad.coopmart.model

import android.os.Parcelable
import com.pujaad.coopmart.api.body.PembelianBody
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pembelian(
    var id: Int,
    var idKaryawan: Int,
    var karyawan: Karyawan?,
    var totalPrice: Int,
    var date: String,
    var items: List<PembelianItem>,
) : Parcelable {
    fun toPembelianBody(): PembelianBody = PembelianBody(
        id_karyawan = idKaryawan,
        items = items.map { it.toPembelianItemBody() }
    )
}