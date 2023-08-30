package com.pujaad.coopmart.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.model.PembelianItem
import com.pujaad.coopmart.model.Product
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent


class AddPembelianItemViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)

    private lateinit var userProfile: Karyawan

    val observableProductName: SingleLiveEvent<String> = SingleLiveEvent()
    val observableQuantity: SingleLiveEvent<Int> = SingleLiveEvent()
    val observableMaxQuantity: SingleLiveEvent<Int> = SingleLiveEvent()
    val observableItemPrice: SingleLiveEvent<Int> = SingleLiveEvent()
    val observableFormActiveState: SingleLiveEvent<Boolean> = SingleLiveEvent()

    var isAddFormState = true

    lateinit var selectedPembelianItem: PembelianItem

    fun setPembelianItem(selectedProduct: Product) {
        selectedPembelianItem = selectedProduct.toNewPembelianItem()

        observableProductName.value = selectedProduct.name
        observableQuantity.value = selectedPembelianItem.quantity
        observableItemPrice.value = selectedPembelianItem.price
        observableFormActiveState.value = true
    }

    fun increasePembelianItemQuantity() {
        selectedPembelianItem.quantity += 1
        observableQuantity.value = selectedPembelianItem.quantity
    }

    fun decreasePembelianItemQuantity() {
        if (selectedPembelianItem.quantity - 1 < 0) {
            return
        }

        selectedPembelianItem.quantity -= 1
        observableQuantity.value = selectedPembelianItem.quantity
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddPembelianItemViewModel(application) as T
        }
    }

}