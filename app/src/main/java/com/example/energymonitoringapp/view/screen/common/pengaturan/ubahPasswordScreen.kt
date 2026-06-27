package com.example.energymonitoringapp.view.screen.common.pengaturan

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.view.components.common.PasswordTextField
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ubahPasswordScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    var passwordLama by remember { mutableStateOf("") }
    var passwordBaru by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }

    var showPasswordLama by remember { mutableStateOf(false) }
    var showPasswordBaru by remember { mutableStateOf(false) }
    var showKonfirmasi by remember { mutableStateOf(false) }

    var errorPasswordBaru by remember { mutableStateOf("") }
    var errorKonfirmasi by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    val r = rememberResponsiveDimensions()

    LaunchedEffect(updateSuccess) {
        if (updateSuccess != null) showSuccessDialog = true
    }

    fun validate(): Boolean {
        var valid = true
        errorPasswordBaru = ""
        errorKonfirmasi = ""
        if (passwordBaru.length < 6) { errorPasswordBaru = "Password baru minimal 6 karakter"; valid = false }
        if (konfirmasiPassword != passwordBaru) { errorKonfirmasi = "Konfirmasi password tidak cocok"; valid = false }
        return valid
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
            Text("Ubah Password", fontWeight = FontWeight.Bold, fontSize = r.screenTitleSize, color = TextPrimary)
            Spacer(modifier = Modifier.height(28.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = r.notifCardFontSize,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Text("Masukkan password lama", fontSize = r.formLabelSize, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value = passwordLama,
                onValueChange = { passwordLama = it; viewModel.clearMessages() },
                placeholder = "Password lama",
                showPassword = showPasswordLama,
                onToggleVisibility = { showPasswordLama = !showPasswordLama },
                errorMessage = ""
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text("Masukkan password baru", fontSize = r.formLabelSize, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value = passwordBaru,
                onValueChange = { passwordBaru = it; if (errorPasswordBaru.isNotEmpty()) errorPasswordBaru = "" },
                placeholder = "Password baru",
                showPassword = showPasswordBaru,
                onToggleVisibility = { showPasswordBaru = !showPasswordBaru },
                errorMessage = errorPasswordBaru
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                value = konfirmasiPassword,
                onValueChange = { konfirmasiPassword = it; if (errorKonfirmasi.isNotEmpty()) errorKonfirmasi = "" },
                placeholder = "Konfirmasi password baru",
                showPassword = showKonfirmasi,
                onToggleVisibility = { showKonfirmasi = !showKonfirmasi },
                errorMessage = errorKonfirmasi
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (validate()) {
                        viewModel.changePassword(passwordLama, passwordBaru, konfirmasiPassword)
                    }
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

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            shape = RoundedCornerShape(16.dp),
            containerColor = White,
            icon = {
                Icon(Icons.Default.CheckCircle, null, tint = GreenPrimary, modifier = Modifier.size(r.dialogIconSize))
            },
            title = { Text("Berhasil!", fontWeight = FontWeight.Bold, fontSize = r.dialogTitleSize, color = TextPrimary) },
            text = {
                Text(
                    text = updateSuccess ?: "Password telah berhasil diperbarui.",
                    fontSize = r.dialogBodySize, color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.clearMessages()
                        onSaveSuccess()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("OK", color = White, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}