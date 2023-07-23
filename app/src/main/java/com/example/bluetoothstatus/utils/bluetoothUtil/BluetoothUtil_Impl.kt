package com.example.bluetoothstatus.utils.bluetoothUtil

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.bluetoothstatus.utils.permissionUtil.BluetoothPermissionUtil
import com.example.bluetoothstatus.utils.permissionUtil.PermissionStatus
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class BluetoothUtil_Impl @Inject constructor(
    @ActivityContext context: Context,
    private val permissionUtil: BluetoothPermissionUtil
) : BluetoothUtil() {

    private val context: ComponentActivity = context as ComponentActivity
    private val registry: ActivityResultRegistry =
        (context as ComponentActivity).activityResultRegistry

    private val bluetoothAdapter: BluetoothAdapter?
    private lateinit var turnOnBluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private var onPermissionResult: ((Boolean) -> Unit)? = null
    private var isPermissionRequestedFromAppSettings = false


    init {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        refreshStatus()
    }

    override fun refreshStatus() {
        if (bluetoothAdapter == null) {
            _status.value = BLUETOOTH_STATUS.NOT_SUPPORTED
        } else if (!bluetoothAdapter.isEnabled) {
            _status.value = BLUETOOTH_STATUS.OFF
        } else {
            _status.value = BLUETOOTH_STATUS.ON
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                register(source)
            }

            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                if (isPermissionRequestedFromAppSettings) {
                    onPermissionResult?.invoke(permissionUtil.getPermissionStatus() == PermissionStatus.Granted)
                }
            }

            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {}
            Lifecycle.Event.ON_ANY -> {}
        }
    }

    private fun register(owner: LifecycleOwner) {
        turnOnBluetoothLauncher = registry.register(
            "TurnOnBluetoothLauncher",
            owner,
            ActivityResultContracts.StartActivityForResult()
        ) {
            //no use because there is broadcast listening for changes
        }
        permissionLauncher = registry.register(
            "BluetoothConnectLauncher", owner,
            ActivityResultContracts.RequestPermission()
        ) { isPermissionGranted ->
            onPermissionResult?.invoke(isPermissionGranted)
        }
    }

    @SuppressLint("InlinedApi")
    override fun turnOnBluetooth(
        onRequestDisplayRational: ((onRationaleAccepted: () -> Unit) -> Unit),
        onPermissionDenied: () -> Unit
    ) {
        val onPermissionResult: (Boolean) -> Unit = { isPermissionGranted ->
            if (isPermissionGranted) {
                turnOnBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else {
                onPermissionDenied.invoke()
            }
        }
        if (!bluetoothAdapter!!.isEnabled) {
            when (val status = permissionUtil.getPermissionStatus()) {
                PermissionStatus.Granted -> {
                    turnOnBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                }

                PermissionStatus.NotRequested -> {
                    this.onPermissionResult =
                        onPermissionResult // initialize before requesting permission
                    this.isPermissionRequestedFromAppSettings = false
                    permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }

                is PermissionStatus.NotGranted -> {
                    this.onPermissionResult =
                        onPermissionResult // initialize before requesting permission
                    if (!status.isRationalShown) {
                        permissionUtil.markRationaleShown() //mark that the rationale has been shown
                        onRequestDisplayRational {
                            this.isPermissionRequestedFromAppSettings = false
                            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                        }
                    } else {
                        this.isPermissionRequestedFromAppSettings = true
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                            it.data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

}