package com.pujaad.coopmart.view.main_menu.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pujaad.coopmart.api.Prefs
import com.ppapujasera_mhu.base.BaseViewModel

class HomeViewModel(application: Application) : BaseViewModel() {

    private val pref = Prefs(application.applicationContext)

    private val _username = MutableLiveData<String>().apply {
        value = pref.user
    }

    val observableUsername: LiveData<String> = _username

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(application) as T
        }
    }
}