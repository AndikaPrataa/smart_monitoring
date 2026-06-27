package com.example.energymonitoringapp.view.components.teknisi

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.view.navigation.Routes
import com.example.energymonitoringapp.view.theme.GreenDark
import com.example.energymonitoringapp.view.theme.GreenLight
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun TeknisiBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val r = rememberResponsiveDimensions()
    val tabs = listOf(
        Triple("Beranda", Icons.Default.Home, 0),
        Triple("Notifikasi", Icons.Default.Notifications, 1),
        Triple("Pengaturan", Icons.Default.Settings, 2)
    )

    NavigationBar(
        containerColor = GreenLight,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { (label, icon, index) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = {
                    onTabSelected(index)
                    when (index) {
                        0 -> onNavigateTo(Routes.DASHBOARD_TEKNISI)
                        1 -> onNavigateTo(Routes.NOTIFIKASI_TEKNISI)
                        2 -> onNavigateTo(Routes.PENGATURAN_TEKNISI)
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(r.navIconSize)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = r.navFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenDark,
                    selectedTextColor = GreenDark,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
