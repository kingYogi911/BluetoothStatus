package com.example.bluetoothstatus.utils.permissionUtil

sealed class PermissionStatus{
    object Granted:PermissionStatus()
    object NotRequested:PermissionStatus()
    class NotGranted(
        val isRationalShown:Boolean
    ):PermissionStatus()
}