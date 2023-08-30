package com.pujaad.coopmart.api.body

data class ChangePasswordBody(
    var id: Int = 0,
    var old_password: String = "",
    var new_password: String = "",
)