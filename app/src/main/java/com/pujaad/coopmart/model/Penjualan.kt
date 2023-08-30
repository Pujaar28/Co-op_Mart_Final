package com.pujaad.coopmart.model

import android.os.Parcelable
import com.pujaad.coopmart.api.body.PenjualanBody
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Penjualan(
    var id: Int,
    var idKaryawan : Int,
    var karyawan: Karyawan? = null,
    var totalPrice: Int,
    var paymentReceived: Int,
    var paymentChanges: Int,
    var customerName: String,
    var date: String,
    var items: List<PenjualanItem>,
) : Parcelable {
    fun toPenjualanBody(): PenjualanBody = PenjualanBody(
        id_karyawan = idKaryawan,
        customer_name = customerName,
        payment = paymentReceived,
        items = items.map { it.toPenjualanItemBody() }
    )
}