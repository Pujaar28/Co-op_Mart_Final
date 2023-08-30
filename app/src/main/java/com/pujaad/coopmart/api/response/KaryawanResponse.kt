package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.Karyawan

data class KaryawanListResponse(
    val result: List<KaryawanResponse>
) {
    fun toKaryawanList(): List<Karyawan> = this.result.map { it.toKaryawan() }
}

data class KaryawanResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("username")
    val username: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("type")
    val type: Int = 0,
    @SerializedName("age")
    val age: Int,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("address")
    val address: String,
) {
    fun toKaryawan(): Karyawan =
        Karyawan(
            id = this.id,
            username = this.username,
            name = this.name,
            type = this.type,
            age = this.age,
            phone = this.phone,
            address = this.address
        )
}
