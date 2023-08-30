package com.pujaad.coopmart.api.body

data class ReportBody(
    var id_karyawan: Int = 0,
    var start_date: String = "",
    var end_date: String = "",
    var type: Int = 0,
    var jumlah_stock:Int =0
)