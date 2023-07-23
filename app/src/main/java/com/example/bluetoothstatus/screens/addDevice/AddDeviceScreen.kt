package com.example.bluetoothstatus.screens.addDevice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothstatus.MainViewModel
import com.example.bluetoothstatus.ui.theme.BluetoothStatusTheme

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun Preview() {
    BluetoothStatusTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AddDeviceScreen(
                navController = rememberNavController(),
                mainViewModel = MainViewModel(),
            )
        }
    }
}

@Composable
fun AddDeviceScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Add Device Screen")
    }
}