package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.ProductCategory

data class ProductCategoryListResponse(
    val result: List<ProductCategoryResponse>
) {
    fun toCategoryList(): List<ProductCategory> = this.result.map { it.toProductCategory() }
}

data class ProductCategoryResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String? = null,
) {
    fun toProductCategory(): ProductCategory =
        ProductCategory(
            id = this.id,
            name = this.name,
            description = description ?: ""
        )
}
