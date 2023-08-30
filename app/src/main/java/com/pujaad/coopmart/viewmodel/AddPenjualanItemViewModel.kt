package com.pujaad.coopmart.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.common.*
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.model.PenjualanItem
import com.pujaad.coopmart.model.Product
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent


class AddPenjualanItemViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)

    private lateinit var userProfile: Karyawan

    val observableProductName: SingleLiveEvent<String> = SingleLiveEvent()
    val observableQuantity: SingleLiveEvent<Int> = SingleLiveEvent()
    val observableMaxQuantity: SingleLiveEvent<Int> = SingleLiveEvent()
    val observableItemPrice: SingleLiveEvent<Int> = SingleLiveEvent()
    val observableFormActiveState: SingleLiveEvent<Boolean> = SingleLiveEvent()

    var maxStock = 0

    var isAddFormState = true

    lateinit var selectedPenjualanItem: PenjualanItem

    fun setPenjualanItem(selectedProduct: Product) {
        selectedPenjualanItem = selectedProduct.toNewPenjualanItem()
        maxStock = selectedPenjualanItem.product?.stock ?: 0

        observableProductName.value = selectedProduct.name
        observableQuantity.value = selectedPenjualanItem.quantity
        observableMaxQuantity.value = maxStock
        observableItemPrice.value = selectedPenjualanItem.price
        observableFormActiveState.value = true
    }

    fun increasePenjualanItemQuantity() {
        if (selectedPenjualanItem.quantity + 1 > maxStock) {
            onError.value = AppError(ErrorType.STOCK_NOT_ENOUGH, "Not enough stock available for your request")
            return
        }
        selectedPenjualanItem.quantity += 1
        observableQuantity.value = selectedPenjualanItem.quantity
    }

    fun decreasePenjualanItemQuantity() {
        if (selectedPenjualanItem.quantity - 1 < 0) {
            return
        }

        selectedPenjualanItem.quantity -= 1
        observableQuantity.value = selectedPenjualanItem.quantity
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddPenjualanItemViewModel(application) as T
        }
    }

}