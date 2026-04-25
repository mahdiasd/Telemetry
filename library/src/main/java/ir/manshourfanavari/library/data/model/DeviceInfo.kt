package ir.manshourfanavari.library.data.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
@SuppressLint("UnsafeOptInUsageError")

@Serializable
data class DeviceInfo(
    val platform: String,

    val os: String,

    val osVersion: String,

    val deviceModel: String
)