package com.example.bluetoothstatus.utils.bluetoothUtil

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LifecycleEventObserver
import com.example.bluetoothstatus.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BluetoothUtil : LifecycleEventObserver {
    protected val _status: MutableStateFlow<BLUETOOTH_STATUS> =
        MutableStateFlow(BLUETOOTH_STATUS.UNKNOWN)
    val status get() = _status.asStateFlow()

    protected val _pairedDevices:MutableStateFlow<List<BluetoothDevice>> = MutableStateFlow(emptyList())
    val pairedDevices get() = _pairedDevices.asStateFlow()

    abstract fun refreshStatus()
    abstract fun turnOnBluetooth(
        onRequestDisplayRational: ((onRationaleAccepted: () -> Unit) -> Unit),
        onPermissionDenied: () -> Unit ={}
    )
}