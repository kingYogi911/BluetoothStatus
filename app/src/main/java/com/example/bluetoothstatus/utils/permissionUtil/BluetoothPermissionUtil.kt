package com.example.bluetoothstatus.utils.permissionUtil

interface BluetoothPermissionUtil {
    fun getPermissionStatus():PermissionStatus
    fun markRationaleShown()
}