package com.example.bluetoothstatus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothstatus.screens.Screens
import com.example.bluetoothstatus.screens.addDevice.AddDeviceScreen
import com.example.bluetoothstatus.screens.dashboard.DashboardScreen
import com.example.bluetoothstatus.ui.theme.BluetoothStatusTheme
import com.example.bluetoothstatus.utils.bluetoothUtil.BluetoothUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var bluetoothUtil: BluetoothUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(bluetoothUtil)
        setContent {
            BluetoothStatusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()
                    NavHost(navController, startDestination = Screens.DASHBOARD.name) {
                        composable(Screens.DASHBOARD.name) {
                            DashboardScreen(navController, viewModel, bluetoothUtil)
                        }
                        composable(Screens.ADD_DEVICE.name) {
                            AddDeviceScreen(navController, viewModel)
                        }
                    }
                }
            }
        }
        startObserving()
    }

    private fun startObserving() {
        lifecycleScope.launch {
            bluetoothUtil.status.collectLatest {
                viewModel.updateBluetoothStatus(it)
            }
        }
        lifecycleScope.launch {
            bluetoothUtil.pairedDevices.collectLatest {
                viewModel.updateBondedDevices(it)
            }
        }
    }
}
