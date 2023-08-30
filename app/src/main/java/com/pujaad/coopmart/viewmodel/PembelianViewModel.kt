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
import com.pujaad.coopmart.model.Pembelian
import com.pujaad.coopmart.model.PembelianItem
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent
import kotlinx.coroutines.launch


class PembelianViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository

    private lateinit var userProfile: Karyawan

    var isAddFormState = true

    lateinit var selectedPembelian: Pembelian
    private val pembelianItems: MutableList<PembelianItem> = mutableListOf()
    private var invoiceId: Int = 0

    val searchHandler: Handler = Handler(Looper.getMainLooper())
    val TYPING_DELAY: Long = 1000

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservablePembelian: MutableLiveData<Pembelian> =
        MutableLiveData()
    private var mutableObservablePembelians: MutableLiveData<Resource<List<Pembelian>>> =
        MutableLiveData()
    private var mutableObservablePembelianItems: MutableLiveData<List<PembelianItem>> =
        MutableLiveData()

    val observablePembelian: LiveData<Pembelian> = mutableObservablePembelian
    val observablePembelians: LiveData<Resource<List<Pembelian>>> = mutableObservablePembelians
    val observablePembelianItems: LiveData<List<PembelianItem>> = mutableObservablePembelianItems
    val onSuccessSubmit: SingleLiveEvent<Pair<Boolean, Int>> = SingleLiveEvent()
    val observableTotalPrice: SingleLiveEvent<String> = SingleLiveEvent()

    fun setPembelian(pembelian: Pembelian?) {
        if (pembelian == null) {
            selectedPembelian = Pembelian(
                id = 0,
                date = "",
                totalPrice = 0,
                items = pembelianItems,
                idKaryawan = prefs.id?.toInt() ?: 0,
                karyawan = null
            )
            return
        }
        selectedPembelian = pembelian
        mutableObservablePembelian.value = pembelian!!
        isAddFormState = false
    }

    fun lookupPembelian(invoiceId: String? = null) {

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
                            invoiceId.lowercase() == "pm" ||
                            invoiceId.lowercase() == "pm-") && invId == 0
                ) {
                    invId = null
                }
            }

            when (val res = repository.getPembelian(invId)) {
                is Outcome.Success -> {
                    try {
                        val pembelians = res.value.toPembelianList()
                        mutableObservablePembelians.value = Resource(
                            ResourceState.SUCCESS,
                            pembelians
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
        pembelianItems.forEach {
            total += it.price * it.quantity
        }

        return total
    }

    fun addPembelianItem(pembelianItem: PembelianItem) {
        var isContentExist = false
        pembelianItems.map {
            if (it.idProduct == pembelianItem.idProduct) {
                it.quantity += pembelianItem.quantity
                observableTotalPrice.value = calculateTotalPrice().toIDRFormat()
                isContentExist = true
            }
            it
        }

        if (isContentExist) {
            mutableObservablePembelianItems.value = pembelianItems
            return
        }
        pembelianItems.add(pembelianItem)
        observableTotalPrice.value = calculateTotalPrice().toIDRFormat()
        mutableObservablePembelianItems.value = pembelianItems
    }

    fun deletePembelianItem(pembelianItem: PembelianItem) {
        pembelianItems.remove(pembelianItem)
        observableTotalPrice.value = calculateTotalPrice().toIDRFormat()
        mutableObservablePembelianItems.value = pembelianItems
    }

    fun submitPembelian() {
        val id = prefs.id?.toInt() ?: 0
        when {
            pembelianItems.isEmpty() -> {
                onError.value = AppError(
                    ErrorType.EMPTY_FIELD, "Produk tidak boleh kosong"
                )
                return
            }

            id == 0 -> {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return
            }

            selectedPembelian == null -> {
                onError.value = AppError(
                    ErrorType.EMPTY_FIELD, "Pembelian data must not empty"
                )
                return
            }
        }

        viewModelScope.launch {
            selectedPembelian = Pembelian(
                id = 0,
                idKaryawan = id,
                karyawan = null,
                totalPrice = 0,
                date = "",
                items = pembelianItems
            )
            isLoading.value = true
            when (val res =
                repository.addPembelian(pembelianBody = selectedPembelian!!.toPembelianBody())
            ) {
                is Outcome.Success -> {
                    invoiceId = res.value.invoiceId
                    onSuccessSubmit.value = Pair(true, invoiceId)
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")

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

    fun setInvoiceData(idPembelian: Int) {
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
            when (val res = repository.getPembelianInvoice(idPembelian)) {
                is Outcome.Success -> {
                    try {
                        invoiceId = idPembelian
                        val result = res.value.toPembelian()
                        mutableObservablePembelian.value = result
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

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PembelianViewModel(application) as T
        }
    }

}