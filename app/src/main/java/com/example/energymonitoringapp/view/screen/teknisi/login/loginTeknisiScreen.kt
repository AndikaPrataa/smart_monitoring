package com.example.energymonitoringapp.view.screen.teknisi.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.view.components.common.EcoUnilaLogo
import com.example.energymonitoringapp.view.components.common.LoginForm
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.RedPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.AuthUiState
import com.example.energymonitoringapp.viewmodel.AuthViewModel

@Composable
fun LoginTeknisiScreen(
    onLoginSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val r = rememberResponsiveDimensions()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passError by remember { mutableStateOf("") }

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
            authViewModel.resetState()
        }
    }

    fun validate(): Boolean {
        emailError = if (email.isBlank()) "Email tidak boleh kosong" else ""
        passError = if (password.isBlank()) "Password tidak boleh kosong" else ""
        return emailError.isEmpty() && passError.isEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = r.horizontalPadding)
    ) {
        Spacer(modifier = Modifier.height(if (r.screenWidthDp < 400.dp) 40.dp else 60.dp))
        EcoUnilaLogo()
        Spacer(modifier = Modifier.height(28.dp))

        if (uiState is AuthUiState.Error) {
            Text(
                text = (uiState as AuthUiState.Error).message,
                color = RedPrimary,
                fontSize = r.fontLabel,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
        }

        LoginForm(
            title = "Masuk (Teknisi)",
            email = email,
            onEmailChange = { email = it; emailError = "" },
            emailError = emailError,
            password = password,
            onPassChange = { password = it; passError = "" },
            passError = passError,
            showPassword = showPassword,
            onTogglePass = { showPassword = !showPassword },
            onLoginClick = {
                if (validate()) {
                    authViewModel.login(email = email, password = password, expectedRole = SessionManager.ROLE_TEKNISI)
                }
            }
        )

        if (uiState is AuthUiState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginTeknisiScreenPreview() {
    EnergyMonitoringAppTheme { LoginTeknisiScreen() }
}
