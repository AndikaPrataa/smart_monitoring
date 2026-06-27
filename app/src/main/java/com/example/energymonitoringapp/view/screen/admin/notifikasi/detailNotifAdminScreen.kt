package com.example.energymonitoringapp.view.screen.admin.notifikasi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.data.response.ListTeknisiResponse
import com.example.energymonitoringapp.data.response.NotifAdminItem
import com.example.energymonitoringapp.data.response.TeknisiItem
import com.example.energymonitoringapp.utils.formatTimestamp
import com.example.energymonitoringapp.utils.getBatasNormal
import com.example.energymonitoringapp.view.components.common.DetailNotifData
import com.example.energymonitoringapp.view.components.common.DetailPenyelesaianCard
import com.example.energymonitoringapp.view.components.common.InfoCard
import com.example.energymonitoringapp.view.components.common.StatusProsesCard
import com.example.energymonitoringapp.view.components.common.SuccessDialog
import com.example.energymonitoringapp.view.components.common.TeknisiDropdownWithId
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.GreenTeal
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.RedPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun detailNotifAdminScreen(
    notifId: Int = 1,
    onBackClick: () -> Unit = {},
    onKirimSuccess: () -> Unit = {},
    vm: AdminViewModel = hiltViewModel()
) {
    LaunchedEffect(notifId) {
        if (notifId > 0) vm.fetchNotifDetail(notifId)
    }
    val notif: NotifAdminItem? by vm.notifDetail.collectAsState()
    val teknisiList: ListTeknisiResponse? by vm.teknisiList.collectAsState()
    val isLoading: Boolean by vm.isLoading.collectAsState()
    val isAssigning: Boolean by vm.isAssigning.collectAsState()
    val assignSuccess: Boolean by vm.assignSuccess.collectAsState()
    val assignError: String? by vm.assignError.collectAsState()

    var selectedTeknisi by remember { mutableStateOf<TeknisiItem?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var errorTeknisi by remember { mutableStateOf(false) }
    var showSuksesDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val r = rememberResponsiveDimensions()

    LaunchedEffect(assignSuccess) {
        if (assignSuccess) {
            showSuksesDialog = true
            vm.resetAssignState()
        }
    }

    LaunchedEffect(assignError) {
        assignError?.let { error ->
            snackbarHostState.showSnackbar(error)
            vm.resetAssignState()
        }
    }

    val isBahaya  = notif?.level == "bahaya"
    val isAktif   = notif?.status == "aktif"
    val isProses  = notif?.status == "proses"
    val isSelesai = notif?.status == "selesai"
    val levelColor  = if (isBahaya) RedPrimary else OrangePrimary
    val statusColor = when (notif?.status) {
        "aktif"   -> RedPrimary
        "proses"  -> OrangePrimary
        "selesai" -> GreenTeal
        else      -> BorderGray
    }
    val teknisiItems: List<TeknisiItem> = teknisiList?.data?.filterNotNull() ?: emptyList()

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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData   = snackbarData,
                    containerColor = RedPrimary,
                    contentColor   = White
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Detail Info",
                        fontWeight = FontWeight.Bold,
                        fontSize   = r.topBarTitleSize,
                        color      = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
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
                isLoading -> {
                    Box(
                        modifier         = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                data != null -> {
                    InfoCard(data = data)
                    Spacer(modifier = Modifier.height(20.dp))
                    when {
                        isAktif -> {
                            Text(
                                text       = "Pilih teknisi yang akan memperbaiki",
                                fontSize   = r.formLabelSize,
                                fontWeight = FontWeight.Medium,
                                color      = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TeknisiDropdownWithId(
                                selectedTeknisi   = selectedTeknisi,
                                expanded          = dropdownExpanded,
                                isError           = errorTeknisi,
                                teknisiList       = teknisiItems,
                                onExpandClick     = { dropdownExpanded = true },
                                onDismiss         = { dropdownExpanded = false },
                                onTeknisiSelected = { item ->
                                    selectedTeknisi  = item
                                    dropdownExpanded = false
                                    errorTeknisi     = false
                                }
                            )
                            if (errorTeknisi) {
                                Text(
                                    text     = "Pilih teknisi terlebih dahulu",
                                    color    = RedPrimary,
                                    fontSize = r.fontCaption,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(28.dp))
                            Button(
                                onClick = {
                                    if (selectedTeknisi == null) {
                                        errorTeknisi = true
                                    } else {
                                        val teknisiId = selectedTeknisi!!.id ?: return@Button
                                        vm.assignTeknisi(notifId, teknisiId)
                                    }
                                },
                                enabled  = !isAssigning,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .widthIn(min = r.buttonWidthLarge)
                                    .height(r.buttonHeight),
                                shape  = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenPrimary,
                                    contentColor   = White
                                )
                            ) {
                                if (isAssigning) {
                                    CircularProgressIndicator(
                                        color       = White,
                                        modifier    = Modifier.size(r.iconSizeMedium),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text       = "Kirim ke Teknisi",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = r.buttonFontSize
                                    )
                                }
                            }
                        }
                        isProses -> {
                            StatusProsesCard()
                        }
                        isSelesai -> {
                            DetailPenyelesaianCard(
                                namaTeknisi     = notif?.technician?.name,
                                selesaiPada     = formatTimestamp(notif?.completedAt),
                                tindakan        = notif?.actionTaken?.toString()
                                    ?.takeIf { it != "null" },
                                catatanTambahan = notif?.additionalNote?.toString()
                                    ?.takeIf { it != "null" },
                                fotoUrl         = notif?.fieldPhotoUrl?.toString()
                                    ?.takeIf { it != "null" }
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier         = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Data tidak ditemukan", color = TextSecondary)
                    }
                }
            }
        }
    }
    if (showSuksesDialog) {
        SuccessDialog(
            title   = "Berhasil Dikirim",
            message = "Notifikasi telah diterima oleh teknisi. Parameter akan segera diperbaiki",
            onConfirm = {
                showSuksesDialog = false
                onKirimSuccess()
            }
        )
    }
}
@Preview
@Composable
fun detailNotifAdminScreenPreview() {
    EnergyMonitoringAppTheme { detailNotifAdminScreen() }
}