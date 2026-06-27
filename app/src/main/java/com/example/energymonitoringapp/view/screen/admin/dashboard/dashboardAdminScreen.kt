package com.example.energymonitoringapp.view.screen.admin.dashboard

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
import com.example.energymonitoringapp.utils.formatBiaya
import com.example.energymonitoringapp.view.components.admin.AdminBottomNavBar
import com.example.energymonitoringapp.view.components.admin.DaftarTeknisiCard
import com.example.energymonitoringapp.view.components.common.AdminStatGrid
import com.example.energymonitoringapp.view.components.common.HeaderCard
import com.example.energymonitoringapp.view.components.common.PullToRefreshWrapper
import com.example.energymonitoringapp.view.components.common.SectionTitle
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.viewmodel.AdminViewModel
import com.example.energymonitoringapp.viewmodel.NilaiViewModel

@Composable
fun dashboardAdminScreen(
    onNavigateTo: (String) -> Unit = {},
    sensorViewModel: NilaiViewModel = hiltViewModel(),
    adminViewModel: AdminViewModel = hiltViewModel()
) {

    var selectedTab by remember { mutableIntStateOf(0) }

    val lingkungan by sensorViewModel.lingkungan.collectAsState()
    val daya by sensorViewModel.daya.collectAsState()
    val allNotif by sensorViewModel.allNotif.collectAsState()

    val teknisiList by adminViewModel.teknisiList.collectAsState()

    val isLoading by sensorViewModel.isLoading.collectAsState()
    val isOffline by sensorViewModel.isOffline.collectAsState()

    val ieqScore = lingkungan?.ieq?.score?.toInt() ?: 0
    val ieqStatus = lingkungan?.ieq?.status ?: "-"

    val ike by sensorViewModel.ike.collectAsState()
    val ikeNilai = ike?.ike?.toString() ?: "-"
    val ikeKategori = ike?.kategori ?: "-"

    val energyKwh =
        daya?.biayaRealtime?.energyKwh?.toString() ?: "-"

    val biayaHari =
        formatBiaya(daya?.biayaRealtime?.biaya)

    val notifAktif =
        allNotif.count { it.status == "aktif" }

    val namaTeknisi = teknisiList?.data
        ?.filterNotNull()
        ?.map { it.name ?: "Teknisi" }
        ?: emptyList()

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AdminBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateTo = onNavigateTo
            )
        }
    ) { innerPadding ->

        PullToRefreshWrapper(
            isRefreshing = isLoading,
            isOffline = isOffline,
            onRefresh = {
                sensorViewModel.fetchAll()
                adminViewModel.fetchListTeknisi()
            },
            modifier = Modifier.padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                HeaderCard(
                    role = "admin",
                    lokasi = lingkungan?.data?.lokasi ?: "Ruang Server",
                    lastSync = lingkungan?.data?.waktu ?: "-"
                )

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle("Status Energi Saat Ini")

                AdminStatGrid(
                    ieqScore    = ieqScore,
                    ieqStatus   = ieqStatus,
                    energyKwh   = energyKwh,
                    ikeNilai    = ikeNilai,
                    ikeKategori = ikeKategori,
                    biayaHari   = biayaHari,
                    notifAktif  = notifAktif
                )

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle("Teknisi yang bertugas")

                Spacer(modifier = Modifier.height(8.dp))

                DaftarTeknisiCard(
                    teknisiList = namaTeknisi.ifEmpty {
                        listOf("Memuat...")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardAdminScreenPreview() {
    EnergyMonitoringAppTheme {
        dashboardAdminScreen()
    }
}