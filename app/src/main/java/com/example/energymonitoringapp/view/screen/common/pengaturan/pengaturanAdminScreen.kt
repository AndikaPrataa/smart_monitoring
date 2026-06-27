package com.example.energymonitoringapp.view.screen.common.pengaturan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.view.components.admin.AdminBottomNavBar
import com.example.energymonitoringapp.view.components.common.LogoutDialog
import com.example.energymonitoringapp.view.components.common.MenuRow
import com.example.energymonitoringapp.view.components.common.ProfilCard
import com.example.energymonitoringapp.view.components.common.ScreenTitle
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.DividerColor
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pengaturanAdminScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateTo: (String) -> Unit = {},
    onUbahPasswordClick: () -> Unit = {},
    onEditProfilClick: () -> Unit = {},
    onBatasNilaiClick: () -> Unit = {},
    onKeluarConfirmed: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadProfile() }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var bottomTab by remember { mutableIntStateOf(3) }
    val r = rememberResponsiveDimensions()

    val menuItems = listOf(
        MenuItemData("Ubah Password", Icons.Default.Lock, null),
        MenuItemData("Edit Profil", Icons.Default.AccountBox, null),
        MenuItemData("Batas Nilai Parameter", Icons.Default.Info, null),
        MenuItemData("Keluar Akun", Icons.Default.ExitToApp, null, isLogout = true)
    )

    val menuActions = listOf(
        onUbahPasswordClick,
        onEditProfilClick,
        onBatasNilaiClick,
        { showLogoutDialog = true }
    )

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
                .padding(horizontal = r.horizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ScreenTitle(title = "Pengaturan")

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading && user == null) {
                ProfilCard(
                    nama = "Memuat...",
                    peran = "..."
                )
            } else {
                ProfilCard(
                    nama  = user?.name ?: "-",
                    peran = user?.role?.replaceFirstChar { it.uppercase() } ?: "-"
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Akun",
                fontWeight = FontWeight.Bold,
                fontSize = r.sectionTitleSize,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    menuItems.forEachIndexed { index, item ->
                        MenuRow(
                            item = item,
                            onClick = menuActions[index]
                        )
                        if (index < menuItems.lastIndex) {
                            HorizontalDivider(
                                color = DividerColor,
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                }
            }
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onKeluarConfirmed()
            }
        )
    }
}

@Preview
@Composable
fun penagturanAdminScreenPreview() {
    EnergyMonitoringAppTheme {
        pengaturanAdminScreen()
    }
}