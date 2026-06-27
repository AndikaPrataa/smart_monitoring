package com.example.energymonitoringapp.view.screen.teknisi.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energymonitoringapp.view.components.common.HeaderCard
import com.example.energymonitoringapp.view.components.common.PullToRefreshWrapper
import com.example.energymonitoringapp.view.components.common.SectionTitle
import com.example.energymonitoringapp.view.components.common.StatusEnergiGrid
import com.example.energymonitoringapp.view.components.teknisi.LingkunganCard
import com.example.energymonitoringapp.view.components.teknisi.ListrikCard
import com.example.energymonitoringapp.view.components.teknisi.TeknisiBottomNavBar
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.viewmodel.NilaiViewModel
import com.example.energymonitoringapp.viewmodel.TeknisiViewModel

@Composable
fun dashboardScreen(
    onNavigateTo: (String) -> Unit = {},
    sensorViewModel: NilaiViewModel = hiltViewModel(),
    teknisiViewModel: TeknisiViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val isLoading by sensorViewModel.isLoading.collectAsState()
    val isOffline by sensorViewModel.isOffline.collectAsState()
    val lingkungan by sensorViewModel.lingkungan.collectAsState()

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            TeknisiBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateTo = onNavigateTo
            )
        }
    ) { innerPadding ->
        PullToRefreshWrapper(
            isRefreshing = isLoading,
            isOffline = isOffline,
            onRefresh = { sensorViewModel.fetchAll() },
            modifier = Modifier.padding(innerPadding)
        ) {
            DashboardContent(sensorViewModel = sensorViewModel, teknisiViewModel = teknisiViewModel)
        }
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    sensorViewModel: NilaiViewModel = viewModel(),
    teknisiViewModel: TeknisiViewModel = hiltViewModel()
) {
    val lingkungan by sensorViewModel.lingkungan.collectAsState()
    val daya by sensorViewModel.daya.collectAsState()
    val listrik by sensorViewModel.listrik.collectAsState()
    val isLoading by sensorViewModel.isLoading.collectAsState()

    val ieqScore = lingkungan?.ieq?.score?.toInt() ?: 0
    val ieqStatus = lingkungan?.ieq?.status ?: "-"
    val ike by sensorViewModel.ike.collectAsState()
    val ikeNilai    = ike?.ike?.toString() ?: "-"
    val ikeKategori = ike?.kategori ?: "-"
    val energyKwh = daya?.biayaRealtime?.energyKwh?.toString() ?: "-"
    val totalDaya = daya?.totalActivePower?.value?.toString() ?: "-"
    val notifTeknisi by teknisiViewModel.notifications.collectAsState()
    val notifAktif = notifTeknisi?.data
        ?.filterNotNull()
        ?.count { it.status == "proses" } ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderCard(
            role = "teknisi",
            lokasi = lingkungan?.data?.lokasi ?: "Ruang Server",
            lastSync = lingkungan?.data?.waktu ?: "-"
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Status Energi Saat Ini")

        StatusEnergiGrid(
            ieqScore    = ieqScore,
            ieqStatus   = ieqStatus,
            ikeNilai    = ikeNilai,
            ikeKategori = ikeKategori,
            energyKwh   = energyKwh,
            notifAktif  = notifAktif
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Parameter Nilai")
        Spacer(modifier = Modifier.height(8.dp))

        LingkunganCard(
            suhu = lingkungan?.data?.suhu?.toString() ?: "-",
            kelembapan = lingkungan?.data?.kelembapan?.toString() ?: "-",
            pm25 = lingkungan?.data?.pm25?.toString() ?: "-",
            pm10 = lingkungan?.data?.pm10?.toString() ?: "-",
            gasCo = lingkungan?.data?.gasCo?.toString() ?: "-",
            gasCo2 = lingkungan?.data?.gasCo2?.toString() ?: "-",
            tvoc = lingkungan?.data?.tvoc?.toString() ?: "-",
            cahaya = lingkungan?.data?.cahaya?.toString() ?: "-",
            kebisingan = lingkungan?.data?.kebisingan?.toString() ?: "-"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ListrikCard(
            daya = "${daya?.totalActivePower?.value ?: "-"} ${daya?.totalActivePower?.unit ?: ""}",
            arus = "${listrik?.total?.totalCurrent ?: "-"} A",
            tegangan = "${listrik?.total?.totalVoltage ?: "-"} V"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun dashboardScreenPreview() {
    EnergyMonitoringAppTheme { dashboardScreen() }
}
