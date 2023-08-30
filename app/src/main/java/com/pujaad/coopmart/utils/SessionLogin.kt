package com.pujaad.coopmart.utils

import android.content.Context
import android.content.Intent
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.view.login.Awal


class SessionLogin(var context: Context) {
    var pref: Prefs = Prefs(context)

    fun logoutUser() {
        pref.clear()
        val intent = Intent(context, Awal::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}