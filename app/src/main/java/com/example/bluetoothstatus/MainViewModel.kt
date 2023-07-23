package com.example.bluetoothstatus

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

    fun updateBluetoothStatus(status: BLUETOOTH_STATUS) {
        if (_bluetoothStatus.value != status) {
            _bluetoothStatus.value = status
        }
    }

}