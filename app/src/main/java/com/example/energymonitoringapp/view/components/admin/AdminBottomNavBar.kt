package com.example.energymonitoringapp.view.components.admin

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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.view.navigation.Routes
import com.example.energymonitoringapp.view.theme.GreenDark
import com.example.energymonitoringapp.view.theme.GreenLight
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

data class NavItem(
    val label: String,
    val icon: Any,
    val tabIndex: Int
)

@Composable
fun AdminBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val r = rememberResponsiveDimensions()
    val tabs = listOf(
        NavItem("Beranda", Icons.Default.Home, 0),
        NavItem("Pemantauan", painterResource(R.drawable.outline_eco_24), 1),
        NavItem("Notifikasi", Icons.Default.Notifications, 2),
        NavItem("Pengaturan", Icons.Default.Settings, 3)
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
                        0 -> onNavigateTo(Routes.DASHBOARD_ADMIN)
                        1 -> onNavigateTo(Routes.PEMANTAUAN_ADMIN)
                        2 -> onNavigateTo(Routes.NOTIFIKASI_ADMIN)
                        3 -> onNavigateTo(Routes.PENGATURAN_ADMIN)
                    }
                },
                icon = {
                    val painter = when (icon) {
                        is ImageVector -> rememberVectorPainter(icon)
                        is Painter -> icon
                        else -> return@NavigationBarItem
                    }
                    Icon(
                        painter = painter,
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
