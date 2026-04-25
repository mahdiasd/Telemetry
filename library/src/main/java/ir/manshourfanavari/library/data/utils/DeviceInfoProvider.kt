package ir.manshourfanavari.library.data.utils

import android.os.Build
import ir.manshourfanavari.library.data.model.DeviceInfo
import ir.manshourfanavari.library.data.model.PlatformType

internal object DeviceInfoProvider {

    fun get(): DeviceInfo {

        return DeviceInfo(
            platform = PlatformType.Mobile.name.lowercase(),
            os = "android",
            osVersion = Build.VERSION.RELEASE ?: "unknown",
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
        )
    }
}