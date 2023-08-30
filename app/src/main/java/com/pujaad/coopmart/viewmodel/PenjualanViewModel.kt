package com.pujaad.coopmart.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.pujaad.coopmart.api.ApiFactory
import com.pujaad.coopmart.api.PosApi
import com.pujaad.coopmart.api.PosRepository
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.common.*
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.model.Penjualan
import com.pujaad.coopmart.model.PenjualanItem
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent
import kotlinx.coroutines.launch


class PenjualanViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository

    private lateinit var userProfile: Karyawan

    var isAddFormState = true

    lateinit var selectedPenjualan: Penjualan
    private val penjualanItems: MutableList<PenjualanItem> = mutableListOf()
    private var invoiceId: Int = 0
    var totalPrice = 0
    var userPayment = 0

    val searchHandler: Handler = Handler(Looper.getMainLooper())
    val TYPING_DELAY: Long = 1000

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservablePenjualan: MutableLiveData<Penjualan> =
        MutableLiveData()
    private var mutableObservablePenjualans: MutableLiveData<Resource<List<Penjualan>>> =
        MutableLiveData()
    private var mutableObservablePenjualanItems: MutableLiveData<List<PenjualanItem>> =
        MutableLiveData()
    val observablePenjualan: LiveData<Penjualan> = mutableObservablePenjualan
    val observablePenjualans: LiveData<Resource<List<Penjualan>>> = mutableObservablePenjualans
    val observablePenjualanItems: LiveData<List<PenjualanItem>> = mutableObservablePenjualanItems
    val onSuccessSubmit: SingleLiveEvent<Pair<Boolean, Int>> = SingleLiveEvent()
    val observableAddFormState: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val observableTotalPrice: SingleLiveEvent<String> = SingleLiveEvent()
    val observableTotalChanges: SingleLiveEvent<String> = SingleLiveEvent()

    fun setPenjualan(penjualan: Penjualan?) {
        if (penjualan == null) {
            selectedPenjualan = Penjualan(
                id = 0,
                date = "",
                totalPrice = 0,
                paymentReceived = 0,
                paymentChanges = 0,
                items = penjualanItems,
                idKaryawan = prefs.id?.toInt() ?: 0,
                karyawan = null,
                customerName = ""
            )
            observableAddFormState.value = isAddFormState
            return
        }
        selectedPenjualan = penjualan
        mutableObservablePenjualan.value = penjualan!!
        isAddFormState = false
        observableAddFormState.value = isAddFormState
    }

    fun lookupPenjualan(invoiceId: String? = null) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED,
                    "Unauthorized user detected, please re-login"
                )
                return@launch
            }

            isLoading.value = true
            var invId: Int? = null
            if (!invoiceId.isNullOrEmpty()) {
                invId = try {
                    invoiceId.split(Constant.INVOICE_SEPARATOR)[1].toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                    0
                }

                if ((invoiceId.lowercase() == "p" ||
                            invoiceId.lowercase() == "pj" ||
                            invoiceId.lowercase() == "pj-") && invId == 0
                ) {
                    invId = null
                }
            }

            when (val res = repository.getPenjualan(invId)) {
                is Outcome.Success -> {
                    try {
                        val penjualans = res.value.toPenjualanList()
                        mutableObservablePenjualans.value = Resource(
                            ResourceState.SUCCESS,
                            penjualans
                        )
                    } catch (e: Exception) {
                        onError.value = AppError(
                            ErrorType.OPERATION_FAILED,
                            "Error occured when trying to fetch user data"
                        )
                    }
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")

                        404 -> onError.value = AppError(
                            ErrorType.INVENTORY_NOT_FOUND, "Data not found"
                        )

                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                }

                else -> {
                    onError.value = AppError(
                        ErrorType.OPERATION_FAILED,
                        "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun calculateTotalPrice(): Int {
        var total = 0
        penjualanItems.forEach {
            total += it.price * it.quantity
        }

        return total
    }

    fun calculateTotalChanges(): Int {
        var total = 0
        penjualanItems.forEach {
            total += it.price * it.quantity
        }

        var result = userPayment - total
        if (result < 0) result = 0
        return result
    }

    fun addPenjualanItem(PenjualanItem: PenjualanItem) {
        var isContentExist = false
        penjualanItems.map {
            if (it.idProduct == PenjualanItem.idProduct) {
                it.quantity += PenjualanItem.quantity
                totalPrice = calculateTotalPrice()
                observableTotalPrice.value = totalPrice.toIDRFormat()
                observableTotalChanges.value = calculateTotalChanges().toIDRFormat()
                isContentExist = true
            }
            it
        }

        if (isContentExist) {
            mutableObservablePenjualanItems.value = penjualanItems
            return
        }
        penjualanItems.add(PenjualanItem)
        totalPrice = calculateTotalPrice()
        observableTotalPrice.value = totalPrice.toIDRFormat()
        observableTotalChanges.value = calculateTotalChanges().toIDRFormat()
        mutableObservablePenjualanItems.value = penjualanItems
    }

    fun deletePenjualanItem(PenjualanItem: PenjualanItem) {
        penjualanItems.remove(PenjualanItem)
        totalPrice = calculateTotalPrice()
        observableTotalPrice.value = totalPrice.toIDRFormat()
        observableTotalChanges.value = calculateTotalChanges().toIDRFormat()
        mutableObservablePenjualanItems.value = penjualanItems
    }

    fun submitPenjualan(customerName: String, paymentReceived: Int) {
        val id = prefs.id?.toInt() ?: 0
        when {
            penjualanItems.isEmpty() -> {
                onError.value = AppError(
                    ErrorType.EMPTY_FIELD, "Penjualan item must not empty"
                )
                return
            }

            customerName.isEmpty() -> {
                onError.value = AppError(
                    ErrorType.EMPTY_FIELD, "Customer name  must not empty"
                )
                return
            }

            paymentReceived == 0 -> {
                onError.value = AppError(
                    ErrorType.EMPTY_FIELD, "Customer payment must not empty"
                )
                return
            }

            paymentReceived < totalPrice -> {
                onError.value = AppError(
                    ErrorType.INVALID_PAYMENT, "Customer payment less than Total Transaction Price"
                )
                return
            }

            id == 0 -> {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return
            }

            selectedPenjualan == null -> {
                onError.value = AppError(
                    ErrorType.EMPTY_FIELD, "Penjualan data must not empty"
                )
                return
            }
        }

        viewModelScope.launch {
            selectedPenjualan = Penjualan(
                id = 0,
                customerName = customerName,
                idKaryawan = id,
                karyawan = null,
                totalPrice = 0,
                paymentReceived = paymentReceived,
                paymentChanges = 0,
                date = "",
                items = penjualanItems
            )
            isLoading.value = true
            when (val res =
                repository.addPenjualan(penjualanBody = selectedPenjualan.toPenjualanBody())
            ) {
                is Outcome.Success -> {
                    invoiceId = res.value.invoiceId
                    onSuccessSubmit.value = Pair(true, invoiceId)
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")
                        501 -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Invalid total payment detected")
                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                }

                else -> {
                    onError.value = AppError(
                        ErrorType.OPERATION_FAILED, "Failed to do operation, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun setInvoiceData(idPenjualan: Int) {
        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED,
                    "Unauthorized user detected, please re-login"
                )
                return@launch
            }

            isLoading.value = true
            when (val res = repository.getPenjualanInvoice(idPenjualan)) {
                is Outcome.Success -> {
                    try {
                        invoiceId = idPenjualan
                        val result = res.value.toPenjualan()
                        mutableObservablePenjualan.value = result
                    } catch (e: Exception) {
                        onError.value = AppError(
                            ErrorType.OPERATION_FAILED,
                            "Error occured when trying to fetch user data"
                        )
                    }
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")

                        404 -> onError.value = AppError(
                            ErrorType.NOT_FOUND, "Invoice data not found"
                        )

                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                }

                else -> {
                    onError.value = AppError(
                        ErrorType.OPERATION_FAILED,
                        "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun getFilename(code: String): String {
        return "invoice_$code-$invoiceId"
    }

    fun getName(): String {
        return prefs.user.toStringOrEmpty()
    }

    fun notifyUserPaymentChange(userPayment: Int) {
        this.userPayment = userPayment
        observableTotalChanges.value = calculateTotalChanges().toIDRFormat()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PenjualanViewModel(application) as T
        }
    }

}