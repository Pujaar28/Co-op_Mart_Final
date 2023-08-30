package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.Supplier

data class SupplierListResponse(
    val result: List<SupplierResponse>
) {
    fun toSupplierList(): List<Supplier> = this.result.map { it.toSupplier() }
}

data class SupplierResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("code")
    val code: String = "",
    @SerializedName("phone")
    val phone: String,
    @SerializedName("address")
    val address: String = "",
    @SerializedName("description")
    val description: String = "",
) {
    fun toSupplier(): Supplier =
        Supplier(
            id = this.id,
            name = this.name,
            code = this.code,
            phone = this.phone,
            address = this.address,
            description = description
        )
}
