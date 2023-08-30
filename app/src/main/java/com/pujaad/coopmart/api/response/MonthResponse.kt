package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.MonthModel

data class MonthListResponse(
    val result: List<MonthResponse>
) {
    fun toMonthModelList(): List<MonthModel> = this.result.map { it.toMonthModel() }
}

data class MonthResponse(
    @SerializedName("id")
    val absenId: Int = 0,
    @SerializedName("id_karyawan")
    val karyawanId: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("date")
    val date: String = ""
) {
    fun toMonthModel(): MonthModel =
        MonthModel(
            id = this.absenId,
            user_id = this.karyawanId,
            name = this.name,
            date = this.date,
        )
}
