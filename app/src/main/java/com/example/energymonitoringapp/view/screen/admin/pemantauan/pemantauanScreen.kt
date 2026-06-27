package com.example.energymonitoringapp.view.screen.admin.pemantauan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.utils.formatBiaya
import com.example.energymonitoringapp.utils.getSensorStatus
import com.example.energymonitoringapp.utils.toColor
import com.example.energymonitoringapp.view.components.admin.AdminBottomNavBar
import com.example.energymonitoringapp.view.components.admin.BiayaHariIniCard
import com.example.energymonitoringapp.view.components.admin.ListrikMiniCard
import com.example.energymonitoringapp.view.components.admin.PemantauanTabRow
import com.example.energymonitoringapp.view.components.admin.SensorCard
import com.example.energymonitoringapp.view.components.admin.SensorCardItem
import com.example.energymonitoringapp.view.components.admin.StatBanner
import com.example.energymonitoringapp.view.components.common.PullToRefreshWrapper
import com.example.energymonitoringapp.view.components.common.ScreenTitle
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.BluePrimary
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.NilaiViewModel

@Composable
fun pemantauanScreen(
    onNavigateTo: (String) -> Unit = {},
    sensorViewModel: NilaiViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var bottomTab by remember { mutableIntStateOf(1) }

    val lingkungan by sensorViewModel.lingkungan.collectAsState()
    val daya by sensorViewModel.daya.collectAsState()
    val listrik by sensorViewModel.listrik.collectAsState()
    val ike by sensorViewModel.ike.collectAsState()
    val isLoading by sensorViewModel.isLoading.collectAsState()
    val isOffline  by sensorViewModel.isOffline.collectAsState()


    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AdminBottomNavBar(
                selectedTab = bottomTab,
                onTabSelected = { bottomTab = it },
                onNavigateTo = onNavigateTo
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScreenTitle(title = "Pemantauan")

            PemantauanTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            if (isLoading && lingkungan == null && daya == null) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                PullToRefreshWrapper(
                    isRefreshing = isLoading,
                    isOffline    = isOffline,
                    onRefresh    = { sensorViewModel.fetchAll() }
                ) {
                    when (selectedTab) {
                        0 -> LingkunganContent(
                            ieqScore   = lingkungan?.ieq?.score?.toInt() ?: 0,
                            ieqStatus  = lingkungan?.ieq?.status ?: "BAIK",
                            suhu       = lingkungan?.data?.suhu,
                            kelembapan = lingkungan?.data?.kelembapan,
                            pm25       = lingkungan?.data?.pm25,
                            pm10       = lingkungan?.data?.pm10,
                            gasCo      = lingkungan?.data?.gasCo,
                            gasCo2     = lingkungan?.data?.gasCo2,
                            cahaya     = lingkungan?.data?.cahaya,
                            kebisingan = lingkungan?.data?.kebisingan,
                            tvoc       = lingkungan?.data?.tvoc
                        )
                        1 -> ListrikContent(
                            ikeNilai    = ike?.ike?.toString() ?: "-",
                            ikeSatuan   = ike?.satuan ?: "kWh/m²/tahun",
                            ikeKategori = ike?.kategori ?: "-",
                            energyKwh   = daya?.biayaRealtime?.energyKwh?.toString() ?: "-",
                            biaya       = formatBiaya(daya?.biayaRealtime?.biaya),
                            tegangan    = "${listrik?.total?.totalVoltage ?: "-"} V",
                            arus        = "${listrik?.total?.totalCurrent ?: "-"} A",
                            daya        = "${daya?.totalActivePower?.value ?: "-"} ${daya?.totalActivePower?.unit ?: ""}"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LingkunganContent(
    ieqScore: Int,
    ieqStatus: String,
    suhu: Double?,
    kelembapan: Double?,
    pm25: Double?,
    pm10: Double?,
    gasCo: Double?,
    gasCo2: Double?,
    cahaya: Double?,
    kebisingan: Double?,
    tvoc: Double?
) {
    val r = rememberResponsiveDimensions()
    // Evaluasi status tiap sensor berdasarkan threshold
    val suhuStatus       = getSensorStatus("suhu", suhu)
    val kelembapanStatus = getSensorStatus("kelembapan", kelembapan)
    val pm25Status       = getSensorStatus("pm25", pm25)
    val pm10Status       = getSensorStatus("pm10", pm10)
    val gasCoStatus      = getSensorStatus("gas_co", gasCo)
    val gasCo2Status     = getSensorStatus("gas_co2", gasCo2)
    val cahayaStatus     = getSensorStatus("cahaya", cahaya)
    val kebisinganStatus = getSensorStatus("kebisingan", kebisingan)
    val tvocStatus       = getSensorStatus("tvoc", tvoc)

    fun Double?.display(): String = this?.toString() ?: "-"

    val lingkunganCards = listOf(
        SensorCard(
            "Udara PM 2,5",
            pm25.display(),
            "ug/m3",
            pm25Status.label,
            R.drawable.baseline_air_24,
            Color(0xFF42A5F5), Color(0xFFE3F2FD),
            statusColor = pm25Status.status.toColor()
        ),
        SensorCard(
            "Udara PM 10",
            pm10.display(),
            "ug/m3",
            pm10Status.label,
            R.drawable.baseline_air_24,
            Color(0xFF42A5F5), Color(0xFFE3F2FD),
            statusColor = pm10Status.status.toColor()
        ),
        SensorCard(
            "Gas CO",
            gasCo.display(),
            "ppm",
            gasCoStatus.label,
            R.drawable.outline_language_us_colemak_24,
            Color(0xFFEF5350), Color(0xFFFFEBEE),
            statusColor = gasCoStatus.status.toColor()
        ),
        SensorCard(
            "Gas CO2",
            gasCo2.display(),
            "ppm",
            gasCo2Status.label,
            R.drawable.outline_co2_24,
            Color(0xFFEF5350), Color(0xFFFFEBEE),
            statusColor = gasCo2Status.status.toColor()
        ),
        SensorCard(
            "Suhu",
            suhu.display(),
            "°C",
            suhuStatus.label,
            R.drawable.baseline_thermostat_24,
            Color(0xFF26C6DA), Color(0xFFE0F7FA),
            statusColor = suhuStatus.status.toColor()
        ),
        SensorCard(
            "Kelembapan",
            kelembapan.display(),
            "%",
            kelembapanStatus.label,
            R.drawable.outline_humidity_percentage_24,
            Color(0xFFEC407A), Color(0xFFFCE4EC),
            statusColor = kelembapanStatus.status.toColor()
        ),
        SensorCard(
            "Cahaya",
            cahaya.display(),
            "lux",
            cahayaStatus.label,
            R.drawable.baseline_wb_incandescent_24,
            Color(0xFFFFA726), Color(0xFFFFF3E0),
            statusColor = cahayaStatus.status.toColor()
        ),
        SensorCard(
            "Kebisingan",
            kebisingan.display(),
            "dBA",
            kebisinganStatus.label,
            R.drawable.baseline_volume_up_24,
            Color(0xFFAB47BC), Color(0xFFF3E5F5),
            statusColor = kebisinganStatus.status.toColor()
        ),
        SensorCard(
            "TVOC",
            tvoc.display(),
            "ppb",
            tvocStatus.label,
            R.drawable.outline_temp_preferences_eco_24,
            Color(0xFF66BB6A), Color(0xFFE8F5E9),
            statusColor = tvocStatus.status.toColor()
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = r.horizontalPadding)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        StatBanner(
            icon = painterResource(id = R.drawable.outline_eco_24),
            label = "Kualitas IEQ",
            backgroundColor = GreenPrimary,
            nilaiText = ieqScore.toString(),
            satuanText = "%",
            statusText = ieqStatus,
            sejajarSatuan = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        lingkunganCards.chunked(r.sensorGridColumns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { card ->
                    SensorCardItem(card = card, modifier = Modifier.weight(1f))
                }
                // Tambah spacer untuk slot kosong agar card tetap sama besar
                repeat(r.sensorGridColumns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ListrikContent(
    ikeNilai: String,
    ikeSatuan: String,
    ikeKategori: String,
    energyKwh: String,
    biaya: String,
    tegangan: String,
    arus: String,
    daya: String
) {
    val r = rememberResponsiveDimensions()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = r.horizontalPadding)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        StatBanner(
            icon = painterResource(id = R.drawable.baseline_battery_charging_full_24),
            label = "Intensitas Konsumsi Energi",
            backgroundColor = OrangePrimary,
            nilaiText = ikeNilai,
            satuanText = ikeSatuan,
            statusText = ikeKategori
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatBanner(
            icon = painterResource(id = R.drawable.baseline_offline_bolt_24),
            label = "Energi Terpakai",
            backgroundColor = BluePrimary,
            nilaiText = energyKwh,
            satuanText = "kWh",
            statusText = ""
        )

        Spacer(modifier = Modifier.height(12.dp))

        BiayaHariIniCard(biaya = biaya)

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ListrikMiniCard(
                label = "Tegangan",
                value = tegangan,
                unit = "",
                status = "Normal",
                modifier = Modifier.weight(1f)
            )
            ListrikMiniCard(
                label = "Arus",
                value = arus,
                unit = "",
                status = "Normal",
                modifier = Modifier.weight(1f)
            )
            ListrikMiniCard(
                label = "Daya",
                value = daya,
                unit = "",
                status = "Normal",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun pemantauanScreenPreview() {
    EnergyMonitoringAppTheme { pemantauanScreen() }
}
