package com.example.bluetoothstatus.di

import com.example.bluetoothstatus.utils.bluetoothUtil.BluetoothUtil
import com.example.bluetoothstatus.utils.bluetoothUtil.BluetoothUtil_Impl
import com.example.bluetoothstatus.utils.permissionUtil.BluetoothPermissionUtil
import com.example.bluetoothstatus.utils.permissionUtil.BluetoothPermissionUtil_Impl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityResultModule {

    @Binds
    abstract fun providesBluetoothPermissionUtil(
        bluetoothPermissionUtil_Impl: BluetoothPermissionUtil_Impl
    ): BluetoothPermissionUtil

    @Binds
    abstract fun providesBluetoothUtil(
        bluetoothUtil_Impl: BluetoothUtil_Impl
    ): BluetoothUtil
}