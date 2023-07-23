package com.example.bluetoothstatus.utils.permissionUtil

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class BluetoothPermissionUtil_Impl @Inject constructor(
    @ActivityContext context: Context
) : BluetoothPermissionUtil {

    var sharedPreference: SharedPreferences

    init {
        sharedPreference =
            context.getSharedPreferences("bluetooth_permission_pref", Context.MODE_PRIVATE)
    }

    private val activity: ComponentActivity = context as ComponentActivity

    override fun getPermissionStatus(): PermissionStatus {
        val isGranted = if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
            true
        } else {
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        }
        return if (isGranted) {
            PermissionStatus.Granted
        } else if (
            !activity.shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT) && !sharedPreference.isRationaleShown()
        ) {
            PermissionStatus.NotRequested
        } else {
            PermissionStatus.NotGranted(sharedPreference.isRationaleShown())
        }
    }

    override fun markRationaleShown() {
        sharedPreference.markRationaleShown()
    }

    private fun SharedPreferences.markRationaleShown() {
        edit().putBoolean("isRationaleShown", true).apply()
    }

    private fun SharedPreferences.isRationaleShown(): Boolean {
        return getBoolean("isRationaleShown", false)
    }
}