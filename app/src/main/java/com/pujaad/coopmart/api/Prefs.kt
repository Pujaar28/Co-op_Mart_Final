package com.pujaad.coopmart.api

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(TOKEN,null)
        set(value) = prefs.edit().putString(TOKEN, value).apply()

    var user: String?
        get() = prefs.getString(USER,null)
        set(value) = prefs.edit().putString(USER, value).apply()

    var id: String?
        get() = prefs.getString(ID,null)
        set(value) = prefs.edit().putString(ID, value).apply()
    var type: String?
        get() = prefs.getString(TYPE, null)
        set(value) = prefs.edit().putString(TYPE, value).apply()

    fun clear() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREFS_FILENAME = "com.pos.prefs"
        private const val TOKEN = "com.pos.prefs.token"
        private const val USER = "com.pos.prefs.user"
        private const val ID = "com.pos.prefs.id"
        private const val TYPE = "com.pos.prefs.type"
    }
}