package com.example.bluetoothstatus.utils.bluetoothUtil

import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BluetoothUtil : LifecycleEventObserver {
    protected val _status: MutableStateFlow<BLUETOOTH_STATUS> =
        MutableStateFlow(BLUETOOTH_STATUS.UNKNOWN)
    val status get() = _status.asStateFlow()

    abstract fun refreshStatus()
    abstract fun turnOnBluetooth(
        onRequestDisplayRational: ((onRationaleAccepted: () -> Unit) -> Unit),
        onPermissionDenied: () -> Unit ={}
    )
}