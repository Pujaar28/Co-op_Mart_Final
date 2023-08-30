package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.Product

data class ProductListResponse(
    val result: List<ProductResponse>
) {
    fun toProductList(): List<Product> = this.result.map { it.toProduct() }
}

data class ProductResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("product_catagory_id")
    val productCategoryId: Int? = null,
    @SerializedName("product_catagory")
    val category: ProductCategoryResponse? = null,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("buy_price")
    val buyPrice: Int = 0,
    @SerializedName("stock")
    val stock: Int = 0,
    @SerializedName("sell_price")
    val sellPrice: Int = 0,
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("supplier")
    val supplier: SupplierResponse? = null,
    @SerializedName("user")
    val user: KaryawanResponse? = null,
) {
    fun toProduct(): Product =
        Product(
            id = this.id,
            productCategoryId = this.productCategoryId,
            category = this.category?.toProductCategory(),
            name = this.name,
            description = description,
            buyPrice = buyPrice,
            sellPrice = sellPrice,
            stock = stock,
            createdAt = this.createdAt,
            supplier = this.supplier?.toSupplier(),
            user = this.user?.toKaryawan()
        )
}
