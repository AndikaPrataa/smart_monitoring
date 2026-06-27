package com.example.energymonitoringapp.view.screen.common.pengaturan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.view.components.common.ProfilFieldLabel
import com.example.energymonitoringapp.view.components.common.ProfilInfoRow
import com.example.energymonitoringapp.view.components.common.ProfileTextField
import com.example.energymonitoringapp.view.components.common.SuccessDialog
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.DisabledText
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editProfilScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val r = rememberResponsiveDimensions()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }


    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {},
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
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Edit Profil", fontWeight = FontWeight.Bold, fontSize = r.screenTitleSize, color = TextPrimary)
            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ProfilInfoRow(label = "Nama",            value = user?.name  ?: "-")
                        HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                        ProfilInfoRow(label = "Peran",           value = user?.role?.replaceFirstChar { it.uppercase() } ?: "-")
                        HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                        ProfilInfoRow(label = "Email",           value = user?.email ?: "-")
                        HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                        ProfilInfoRow(label = "Nomor Karyawan",  value = user?.nip   ?: "-")
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(r.buttonWidthSmall)
                    .height(r.buttonHeight),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = White)
            ) {
                Text("Edit", fontWeight = FontWeight.SemiBold, fontSize = r.buttonFontSize)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilFormScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val r = rememberResponsiveDimensions()

    var nama by remember(user) { mutableStateOf(user?.name ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var nomorKaryawan by remember(user) { mutableStateOf(user?.nip ?: "") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        nama          = user?.name  ?: ""
        email         = user?.email ?: ""
        nomorKaryawan = user?.nip   ?: ""
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess != null) showDialog = true
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {},
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = r.horizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Edit Profil", fontWeight = FontWeight.Bold, fontSize = r.screenTitleSize, color = TextPrimary)
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = r.notifCardFontSize,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            ProfilFieldLabel("Nama")
            Spacer(modifier = Modifier.height(8.dp))
            ProfileTextField(
                value = nama,
                onValueChange = { nama = it },
                placeholder = "Nama lengkap",
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(18.dp))

            ProfilFieldLabel("Peran")
            Spacer(modifier = Modifier.height(8.dp))
            ProfileTextField(
                value = user?.role?.replaceFirstChar { it.uppercase() } ?: "",
                onValueChange = {},
                placeholder = "Peran",
                enabled = false,
                trailingIcon = {
                    Icon(Icons.Default.Lock, "Tidak dapat diubah", tint = DisabledText,
                        modifier = Modifier.size(r.iconSizeMedium))
                }
            )
            Text("Peran tidak dapat diubah", fontSize = r.fontCaption, color = DisabledText,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp))

            Spacer(modifier = Modifier.height(18.dp))

            ProfilFieldLabel("Email")
            Spacer(modifier = Modifier.height(8.dp))
            ProfileTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(18.dp))

            ProfilFieldLabel("Nomor Karyawan")
            Spacer(modifier = Modifier.height(8.dp))
            ProfileTextField(
                value = nomorKaryawan,
                onValueChange = { nomorKaryawan = it },
                placeholder = "Nomor Karyawan",
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.updateProfile(
                        name  = nama.takeIf { it != user?.name },
                        email = email.takeIf { it != user?.email },
                        nip   = nomorKaryawan.takeIf { it != user?.nip }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.align(Alignment.CenterHorizontally).width(r.buttonWidthMedium).height(r.buttonHeight),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = White)
            ) {
                if (isLoading) CircularProgressIndicator(color = White, modifier = Modifier.size(r.iconSizeMedium))
                else Text("Simpan", fontWeight = FontWeight.SemiBold, fontSize = r.buttonFontSize)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDialog) {
        SuccessDialog(
            title = "Berhasil!",
            message = updateSuccess ?: "Profil telah berhasil diperbarui.",
            onConfirm = {
                showDialog = false
                viewModel.clearMessages()
                onSaveSuccess()
            }
        )
    }
}

@Preview(name = "View Profil — Teknisi")
@Composable
fun editProfilScreenPreview() {
    EnergyMonitoringAppTheme {
        editProfilScreen(
        )
    }
}

@Preview(name = "Form Edit — Teknisi")
@Composable
fun editProfilFormScreenPreview() {
    EnergyMonitoringAppTheme {
        EditProfilFormScreen(
        )
    }
}


