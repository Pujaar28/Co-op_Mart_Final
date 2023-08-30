package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.PenjualanItem

data class PenjualanItemListResponse(
    val result: List<PenjualanItemResponse>
) {
    fun toPenjualanItemList(): List<PenjualanItem> = this.result.map { it.toPenjualanItem() }
}

data class PenjualanItemResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("id_penjualan")
    val idPenjualan: Int = 0,
    @SerializedName("id_product")
    val idProduct: Int? = null,
    @SerializedName("product")
    val product: ProductResponse? = null,
    @SerializedName("quantity")
    val quantity: Int = 0,
    @SerializedName("price")
    val price: Int = 0,
) {
    fun toPenjualanItem(): PenjualanItem =
        PenjualanItem(
            id = this.id,
            idPenjualan = this.idPenjualan,
            idProduct = idProduct,
            product = product?.toProduct(),
            quantity = this.quantity,
            price = this.price,
        )
}
