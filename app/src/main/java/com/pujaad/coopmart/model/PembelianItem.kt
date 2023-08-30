package com.pujaad.coopmart.model

import android.os.Parcelable
import com.pujaad.coopmart.api.body.PembelianItemBody
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PembelianItem(
    var id: Int,
    var idPembelian : Int,
    var idSupplier : Int?,
    var supplier : Supplier?,
    var idProduct: Int?,
    var product: Product?,
    var quantity: Int,
    var price: Int,
    var createdAt: String,
) : Parcelable {
    fun toPembelianItemBody(): PembelianItemBody = PembelianItemBody(
        id_supplier = idSupplier!!,
        id_product = idProduct!!,
        quantity = quantity,
        price = price
    )
}