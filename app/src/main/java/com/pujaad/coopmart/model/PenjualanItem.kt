package com.pujaad.coopmart.model

import android.os.Parcelable
import com.pujaad.coopmart.api.body.PenjualanItemBody
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PenjualanItem(
    var id: Int,
    var idPenjualan : Int,
    var idProduct : Int?,
    var product: Product?,
    var quantity: Int,
    var price: Int,
) : Parcelable {
    fun toPenjualanItemBody() : PenjualanItemBody = PenjualanItemBody(
        id_product = product?.id,
        quantity = quantity,
        price = price
    )
}