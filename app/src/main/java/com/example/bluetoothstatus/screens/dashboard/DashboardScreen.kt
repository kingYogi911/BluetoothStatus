package com.example.bluetoothstatus.screens.dashboard

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.example.bluetoothstatus.MainViewModel
import com.example.bluetoothstatus.ui.theme.BluetoothStatusTheme
import com.example.bluetoothstatus.utils.bluetoothUtil.BLUETOOTH_STATUS
import com.example.bluetoothstatus.utils.bluetoothUtil.BluetoothUtil

//@Preview(widthDp = 360, heightDp = 800)
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
            DashboardMainScreen(bluetoothStatus = state,
                rationaleState = rationaleState,
                onRequestBluetoothOn = {
                    state.value = BLUETOOTH_STATUS.ON
                })
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DashboardScreen(
    navController: NavHostController, mainViewModel: MainViewModel, bluetoothUtil: BluetoothUtil
) {
    val bluetoothStatus: State<BLUETOOTH_STATUS> = mainViewModel.bluetoothStatus.collectAsState()
    val rationaleState: MutableState<Pair<Boolean, (() -> Unit)?>> = remember {
        mutableStateOf(false to null)
    }
    val pairedDevices = mainViewModel.pairedDevices.collectAsState()
    if (bluetoothStatus.value != BLUETOOTH_STATUS.ON) {
        DashboardMainScreen(
            bluetoothStatus = bluetoothStatus,
            rationaleState = rationaleState,
            onRefresh = {
                bluetoothUtil.refreshStatus()
            },
            onRequestBluetoothOn = {
                bluetoothUtil.turnOnBluetooth(onRequestDisplayRational = { onRationaleAccepted: () -> Unit ->
                    rationaleState.value = true to {
                        rationaleState.value = false to null
                        onRationaleAccepted.invoke()
                    }
                })
            },
        )
    } else {
        BluetoothEnabledScreen(
            pairedDevices =pairedDevices.value.size,
            newDevices = 0,
            connectedDevices =pairedDevices.value.filter {
                it.bondState == BluetoothDevice.BOND_BONDED
            }.map {
                ConnectedDeviceItem(
                    it.address,
                    it.name
                )
            }
        )
    }
}

@Composable
fun DashboardMainScreen(
    bluetoothStatus: State<BLUETOOTH_STATUS>,
    rationaleState: State<Pair<Boolean, (() -> Unit)?>>,
    onRefresh: () -> Unit = {},
    onRequestBluetoothOn: () -> Unit = {}
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
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
            AlertDialog(onDismissRequest = { }, confirmButton = {
                TextButton(onClick = {
                    rationaleState.value.second?.invoke()
                }) {
                    Text(text = "Ok")
                }
            }, title = {
                Text(text = "Rationale")
            }, text = {
                Text(text = "Bluetooth Permission is required for functioning of App")
            })
        }
    }

}


@Preview(widthDp = 360, heightDp = 800)
@Composable
fun BluetoothEnabledScreenPreview() {
    val testItems = listOf(
        ConnectedDeviceItem(
            "121", "Rockers 999"
        )
    )
    BluetoothStatusTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            BluetoothEnabledScreen(
                5, 2, testItems
            )
        }
    }
}

@Composable
fun BluetoothEnabledScreen(
    pairedDevices: Int,
    newDevices: Int,
    connectedDevices: List<ConnectedDeviceItem>,
    onClickPairedDevices: () -> Unit = {},
    onClickDiscoveredDevices: () -> Unit = {},
    onClickRefresh: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth(0.5f),
                shape = RoundedCornerShape(5.dp),
                colors = buttonColors(containerColor = Color("#0E4D92".toColorInt())),
                onClick = onClickPairedDevices
            ) {
                Text(
                    text = "$pairedDevices\nPaired Devices", textAlign = TextAlign.Center
                )
            }

            Button(
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                colors = buttonColors(containerColor = Color("#E25825".toColorInt())),
                onClick = onClickDiscoveredDevices
            ) {
                Text(
                    text = "$newDevices\nDiscovered Devices", textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Connected Devices :",
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )
            Icon(
                painter = rememberVectorPainter(image = Icons.Filled.Refresh),
                contentDescription = "",
                modifier = Modifier.clickable {
                    onClickRefresh.invoke()
                }
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        LazyColumn {
            items(items = connectedDevices) { item ->
                ConnectedDeviceComponent(item)
            }
        }
    }
}

@Composable
fun ConnectedDeviceComponent(
    item: ConnectedDeviceItem
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
    ) {
        Image(
            painter = rememberVectorPainter(image = Icons.Filled.Call),
            contentDescription = "",
            modifier = Modifier
                .size(50.dp)
                .padding(5.dp),
            contentScale = ContentScale.FillBounds
        )
        Divider(
            color = Color.Gray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Text(
            text = "${item.itemName}\nID : ${item.itemId}",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        )
    }
}

data class ConnectedDeviceItem(
    val itemId: String, val itemName: String
)