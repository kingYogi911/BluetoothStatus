package com.example.bluetoothstatus.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bluetoothstatus.MainViewModel
import com.example.bluetoothstatus.ui.theme.BluetoothStatusTheme
import com.example.bluetoothstatus.utils.bluetoothUtil.BLUETOOTH_STATUS
import com.example.bluetoothstatus.utils.bluetoothUtil.BluetoothUtil

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun Preview() {
    BluetoothStatusTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val state = remember {
                mutableStateOf(BLUETOOTH_STATUS.OFF)
            }
            val rationaleState = remember {
                mutableStateOf(true to null)
            }
            DashboardMainScreen(
                bluetoothStatus = state,
                rationaleState = rationaleState,
                onRequestBluetoothOn = {
                    state.value = BLUETOOTH_STATUS.ON
                }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    bluetoothUtil: BluetoothUtil
) {
    val bluetoothStatus: State<BLUETOOTH_STATUS> = mainViewModel.bluetoothStatus.collectAsState()
    val rationaleState: MutableState<Pair<Boolean, (() -> Unit)?>> = remember {
        mutableStateOf(false to null)
    }
    DashboardMainScreen(
        bluetoothStatus = bluetoothStatus,
        rationaleState = rationaleState,
        onRefresh = {
            bluetoothUtil.refreshStatus()
        },
        onRequestBluetoothOn = {
            bluetoothUtil.turnOnBluetooth(
                onRequestDisplayRational = { onRationaleAccepted: () -> Unit ->
                    rationaleState.value = true to {
                        rationaleState.value = false to null
                        onRationaleAccepted.invoke()
                    }
                }
            )
        },
    )
}

@Composable
fun DashboardMainScreen(
    bluetoothStatus: State<BLUETOOTH_STATUS>,
    rationaleState: State<Pair<Boolean, (() -> Unit)?>>,
    onRefresh: () -> Unit = {},
    onRequestBluetoothOn: () -> Unit = {},
    onRationaleAccepted: () -> Unit = {}
) {


    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.Blue),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Dashboard", color = Color.White)
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (bluetoothStatus.value) {
                BLUETOOTH_STATUS.UNKNOWN -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Bluetooth Status unknown")
                        Button(onClick = onRefresh) {
                            Text(text = "Refresh")
                        }
                    }
                }

                BLUETOOTH_STATUS.NOT_SUPPORTED -> {
                    Text(text = "Bluetooth Not Supported in the device")
                }

                BLUETOOTH_STATUS.OFF -> {
                    Button(onClick = onRequestBluetoothOn) {
                        Text(text = "Turn on bluetooth")
                    }
                }

                BLUETOOTH_STATUS.ON -> {
                    Text(text = "Bluetooth is On")
                }
            }
        }
        if (rationaleState.value.first) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {
                    TextButton(onClick = {
                        rationaleState.value.second?.invoke()
                    }) {
                        Text(text = "Ok")
                    }
                },
                title = {
                    Text(text = "Rationale")
                },
                text = {
                    Text(text = "Bluetooth Permission is required for functioning of App")
                }
            )
        }
    }
}