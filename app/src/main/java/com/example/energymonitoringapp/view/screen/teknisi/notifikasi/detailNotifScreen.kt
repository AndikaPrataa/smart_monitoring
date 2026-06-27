package com.example.energymonitoringapp.view.screen.teknisi.notifikasi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.utils.formatTimestamp
import com.example.energymonitoringapp.utils.getBatasNormal
import com.example.energymonitoringapp.view.components.common.ConfirmDialog
import com.example.energymonitoringapp.view.components.common.DetailNotifData
import com.example.energymonitoringapp.view.components.common.DetailPenyelesaianCard
import com.example.energymonitoringapp.view.components.common.InfoCard
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.GreenTeal
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.RedPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.TeknisiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun detailNotifScreen(
    notifId: Int = 0,
    onBackClick: () -> Unit = {},
    onKonfirmasiClick: (Int) -> Unit = {},
    vm: TeknisiViewModel = hiltViewModel()
) {
    val r = rememberResponsiveDimensions()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val namaTeknisi = sessionManager.getName()

    LaunchedEffect(notifId) {
        if (notifId > 0) vm.fetchNotifDetail(notifId)
    }

    val notif by vm.notifDetail.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    val isBahaya  = notif?.level == "bahaya"
    val isAktif   = notif?.status == "aktif" || notif?.status == "proses"
    val isSelesai = notif?.status == "selesai"
    val levelColor  = if (isBahaya) RedPrimary else OrangePrimary
    val statusColor = if (isAktif) RedPrimary else GreenTeal

    val data = notif?.let {
        DetailNotifData(
            level         = if (isBahaya) "Bahaya" else "Waspada",
            levelColor    = levelColor,
            status        = it.status?.replaceFirstChar { c -> c.uppercase() } ?: "-",
            statusColor   = statusColor,
            waktuRelative = formatTimestamp(it.timestamp),
            kategori      = it.kategori ?: "-",
            parameterNama = it.sensor ?: "-",
            nilaiSaatIni  = "${it.value ?: "-"} ${it.unit ?: ""}",
            batasNormal   = getBatasNormal(it.sensor),
            lokasi        = it.lokasi ?: "-",
            waktu         = formatTimestamp(it.timestamp)
        )
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Info",
                        fontWeight = FontWeight.Bold,
                        fontSize   = r.topBarTitleSize,
                        color      = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGray)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = r.horizontalPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> CircularProgressIndicator()
                data != null -> {
                    InfoCard(data = data)
                    Spacer(modifier = Modifier.height(24.dp))

                    when {
                        isAktif -> {
                            Button(
                                onClick  = { onKonfirmasiClick(notifId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(r.buttonHeight),
                                shape  = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenPrimary,
                                    contentColor   = White
                                )
                            ) {
                                Text(
                                    "Konfirmasi Penanganan",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = r.buttonFontSize
                                )
                            }
                        }

                        isSelesai -> {
                            DetailPenyelesaianCard(
                                namaTeknisi     = namaTeknisi,
                                selesaiPada     = formatTimestamp(notif?.completedAt),
                                tindakan        = notif?.actionTaken
                                    ?.takeIf { it != "null" },
                                catatanTambahan = notif?.additionalNote
                                    ?.takeIf { it != "null" },
                                fotoUrl         = notif?.fieldPhotoUrl
                                    ?.takeIf { it != "null" }
                            )
                        }
                    }
                }
                else -> Text("Data tidak ditemukan", color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDialog) {
        ConfirmDialog(
            title       = "Konfirmasi Penanganan",
            message     = "Apakah kamu yakin telah menangani notifikasi ini?",
            confirmText = "Ya, Konfirmasi",
            onConfirm   = { showDialog = false; onKonfirmasiClick(notifId) },
            onDismiss   = { showDialog = false }
        )
    }
}

@Preview
@Composable
fun detailNotifScreenPreview() {
    EnergyMonitoringAppTheme { detailNotifScreen() }
}