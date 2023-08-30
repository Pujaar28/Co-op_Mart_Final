package com.pujaad.coopmart.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.pujaad.coopmart.api.ApiFactory
import com.pujaad.coopmart.api.PosApi
import com.pujaad.coopmart.api.PosRepository
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.body.ReportBody
import com.pujaad.coopmart.api.common.*
import com.pujaad.coopmart.api.response.LabaRugiListResponse
import com.pujaad.coopmart.api.response.PembelianListResponse
import com.pujaad.coopmart.api.response.PenjualanListResponse
import com.pujaad.coopmart.api.response.ProductListResponse
import com.pujaad.coopmart.extension.toReportBodyDateFormat
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.model.LabaRugi
import com.pujaad.coopmart.model.Pembelian
import com.pujaad.coopmart.model.Penjualan
import com.pujaad.coopmart.model.Product
import com.ppapujasera_mhu.base.BaseViewModel
import kotlinx.coroutines.launch
import java.io.InputStream


class ReportViewModel(application: Application) : BaseViewModel() {
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository

    private lateinit var userProfile: Karyawan

    var isAddFormState = true

    private var reportBody: ReportBody = ReportBody(
        id_karyawan = 0,
        start_date = "",
        end_date = ""
    )

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservableReport: MutableLiveData<ReportGroup> =
        MutableLiveData()

    private var mutableObservableReportPDF: MutableLiveData<InputStream> =
        MutableLiveData()

    val observableReport: LiveData<ReportGroup> = mutableObservableReport
    val observableReportPDF: LiveData<InputStream> = mutableObservableReportPDF

    fun submitReport(startDate: String, endDate: String, type: Int) {
        val id = prefs.id?.toInt() ?: 0
        when (id) {
            0 -> {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return
            }
        }

        isLoading.value = true
        viewModelScope.launch {
            reportBody = ReportBody(
                id_karyawan = 0,
                start_date = startDate.toReportBodyDateFormat(),
                end_date = endDate.toReportBodyDateFormat(),
                type = type
            )
            when (val res =
                when (type) {
                    0 -> repository.reportPembelian(body = reportBody)
                    1 -> repository.reportPenjualan(body = reportBody)
                    2 -> repository.reportProduct(body = reportBody)
                    else -> repository.reportLabaRugi(body = reportBody)
                }
            ) {
                is Outcome.Success -> {
                    when (type) {
                        0 -> {
                            val response: PembelianListResponse = res.value as PembelianListResponse
                            mutableObservableReport.value = ReportGroup(
                                pembelianList = response.toPembelianList(),
                                penjualanList = arrayListOf(),
                                productList = arrayListOf(),
                                labaRugiList = arrayListOf(),
                            )
                        }
                        1 -> {
                            val response: PenjualanListResponse = res.value as PenjualanListResponse
                            mutableObservableReport.value = ReportGroup(
                                pembelianList = arrayListOf(),
                                penjualanList = response.toPenjualanList(),
                                productList = arrayListOf(),
                                labaRugiList = arrayListOf(),
                            )
                        }
                        2 -> {
                            val response: ProductListResponse = res.value as ProductListResponse
                            mutableObservableReport.value = ReportGroup(
                                pembelianList = arrayListOf(),
                                penjualanList = arrayListOf(),
                                productList = response.toProductList(),
                                labaRugiList = arrayListOf(),
                            )
                        }
                        else -> {
                            val response: LabaRugiListResponse = res.value as LabaRugiListResponse
                            mutableObservableReport.value = ReportGroup(
                                pembelianList = arrayListOf(),
                                penjualanList = arrayListOf(),
                                productList = arrayListOf(),
                                labaRugiList = response.toLabaRugiList(),
                            )
                        }
                    }
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")
                        404 -> onError.value =
                            AppError(ErrorType.NOT_FOUND, "Data report not found")

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


    fun resetReport() {
        reportBody = ReportBody(
            id_karyawan = 0,
            start_date = "",
            end_date = ""
        )

        mutableObservableReport.value = ReportGroup(
            pembelianList = arrayListOf(),
            penjualanList = arrayListOf(),
            productList = arrayListOf(),
            labaRugiList = arrayListOf(),
        )
    }

    fun getResultPDF() {
        val id = prefs.id?.toInt() ?: 0
        when {
            id == 0 -> {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return
            }

            reportBody == null -> {
                onError.value = AppError(
                    ErrorType.NOT_FOUND, "Failed to get report detail"
                )
                return
            }
        }

        isLoading.value = true
        viewModelScope.launch {
            when (val res =
                when (reportBody.type) {
                    0 -> repository.reportPembelianPDF(
                        userId = null,
                        startDate = reportBody.start_date,
                        endDate = reportBody.end_date
                    )
                    1 -> repository.reportPenjualanPDF(
                        userId = null,
                        startDate = reportBody.start_date,
                        endDate = reportBody.end_date
                    )
                    2 -> repository.reportProductPDF(
                        userId = null,
                        startDate = reportBody.start_date,
                        endDate = reportBody.end_date
                    )
                    else -> repository.reportLabaRugiPDF(
                        userId = null,
                        startDate = reportBody.start_date,
                        endDate = reportBody.end_date
                    )
                }
            ) {
                is Outcome.Success -> {
                    val result = res.value
                    val inputStream: InputStream = result.byteStream()
                    mutableObservableReportPDF.value = inputStream
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")
                        404 -> onError.value =
                            AppError(ErrorType.NOT_FOUND, "Data report not found")

                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                    isLoading.value = false
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

    data class ReportGroup(
        val pembelianList: List<Pembelian>,
        val penjualanList: List<Penjualan>,
        val productList: List<Product>,
        val labaRugiList: List<LabaRugi>,
    )

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportViewModel(application) as T
        }
    }

}