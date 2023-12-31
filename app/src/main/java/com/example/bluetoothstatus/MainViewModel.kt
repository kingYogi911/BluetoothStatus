package com.example.bluetoothstatus

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.example.bluetoothstatus.utils.bluetoothUtil.BLUETOOTH_STATUS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _bluetoothStatus = MutableStateFlow<BLUETOOTH_STATUS>(BLUETOOTH_STATUS.UNKNOWN)
    val bluetoothStatus get() = _bluetoothStatus.asStateFlow()

    val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices get() = _pairedDevices.asStateFlow()

    fun updateBluetoothStatus(status: BLUETOOTH_STATUS) {
        if (_bluetoothStatus.value != status) {
            _bluetoothStatus.value = status
        }
    }

    fun updateBondedDevices(devices: List<BluetoothDevice>) {
        _pairedDevices.value = devices
    }

    data class PairedDevice(
        val id: Int,
        val name: String,
        val isConnected:String
    )
}