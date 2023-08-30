package com.pujaad.coopmart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelDatabaseList(
    val data: List<ModelDatabase>
) : Parcelable
