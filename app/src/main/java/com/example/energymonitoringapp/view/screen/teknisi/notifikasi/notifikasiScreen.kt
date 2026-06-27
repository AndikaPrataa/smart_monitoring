package com.example.energymonitoringapp.view.screen.teknisi.notifikasi

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.data.response.NotifTeknisiItem
import com.example.energymonitoringapp.utils.formatTimestamp
import com.example.energymonitoringapp.view.components.common.FilterTabRow
import com.example.energymonitoringapp.view.components.common.IconTextRow
import com.example.energymonitoringapp.view.components.common.NotifColoredSideBar
import com.example.energymonitoringapp.view.components.common.NotifLevelBadge
import com.example.energymonitoringapp.view.components.common.NotifStatusChip
import com.example.energymonitoringapp.view.components.common.PullToRefreshWrapper
import com.example.energymonitoringapp.view.components.common.ScreenTitle
import com.example.energymonitoringapp.view.components.teknisi.TeknisiBottomNavBar
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenLight
import com.example.energymonitoringapp.view.theme.GreenTeal
import com.example.energymonitoringapp.view.theme.OrangeLight
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.RedLight
import com.example.energymonitoringapp.view.theme.RedPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.TeknisiViewModel

private fun isDeviceOfflineTeknisi(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return true
    val caps = cm.getNetworkCapabilities(network) ?: return true
    return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) }

@Composable
fun notifikasiScreen(
    onNavigateTo: (String) -> Unit = {},
    onNotifClick: (Int) -> Unit = {},
    vm: TeknisiViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var bottomTab   by remember { mutableIntStateOf(1) }

    val notifResponse by vm.notifications.collectAsState()
    val isLoading     by vm.isLoading.collectAsState()

    val context        = LocalContext.current
    val r = rememberResponsiveDimensions()
    val lifecycleOwner = LocalLifecycleOwner.current

    val isOffline = remember { mutableStateOf(isDeviceOfflineTeknisi(context)) }

    val allNotif = notifResponse?.data?.filterNotNull() ?: emptyList()

    val filteredNotif = remember(selectedTab, allNotif) {
        when (selectedTab) {
            0    -> allNotif.filter { it.status == "aktif" || it.status == "proses" }
            else -> allNotif.filter { it.status == "selesai" }
        }
    }

    val scrollAktif   = rememberLazyListState()
    val scrollSelesai = rememberLazyListState()
    val currentScroll = if (selectedTab == 0) scrollAktif else scrollSelesai

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    isOffline.value = isDeviceOfflineTeknisi(context)
                    vm.fetchNotifications()
                }
            }
        )
    }

    Scaffold(
        containerColor = White,
        bottomBar = {
            TeknisiBottomNavBar(
                selectedTab   = bottomTab,
                onTabSelected = { bottomTab = it },
                onNavigateTo  = onNavigateTo
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScreenTitle(title = "Notifikasi")

            FilterTabRow(
                tabs          = listOf("Aktif", "Selesai"),
                tabIndices    = listOf(0, 1),
                selectedTab   = selectedTab,
                onTabSelected = { selectedTab = it }
            )


            val offline = isDeviceOfflineTeknisi(context)
            AnimatedVisibility(
                visible = offline,
                enter   = expandVertically(),
                exit    = shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OrangePrimary)
                        .padding(horizontal = r.horizontalPadding, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(r.iconSizeSmall)
                    )
                    Text(
                        text      = "Offline — menampilkan data terakhir yang tersimpan",
                        color     = White,
                        fontSize  = r.notifSmallFontSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && allNotif.isEmpty()) {
                LoadingView()
            } else {
                PullToRefreshWrapper(
                    isRefreshing = isLoading,
                    isOffline    = isOffline.value,
                    onRefresh    = {
                        isOffline.value = isDeviceOfflineTeknisi(context)
                        vm.fetchNotifications()
                    }
                ) {
                    when {
                        isLoading             -> LoadingView()
                        filteredNotif.isEmpty() -> EmptyNotificationView(selectedTab)
                        else -> {
                            LazyColumn(
                                state               = currentScroll,
                                contentPadding      = PaddingValues(
                                    horizontal = r.horizontalPadding,
                                    vertical   = 4.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = filteredNotif,
                                    key   = { it.id ?: 0 }
                                ) { item ->
                                    NotifTeknisiCard(
                                        item    = item,
                                        onClick = { item.id?.let(onNotifClick) }
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyNotificationView(selectedTab: Int) {
    val r = rememberResponsiveDimensions()

    val emptyText =
        if (selectedTab == 0) {
            "Tidak ada notifikasi aktif"
        } else {
            "Tidak ada notifikasi selesai"
        }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(r.emptyStateIconSize)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = emptyText,
                fontSize = r.emptyStateFontSize,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NotifTeknisiCard(
    item: NotifTeknisiItem,
    onClick: () -> Unit = {}
) {
    val r = rememberResponsiveDimensions()

    val isBahaya = item.level == "bahaya"
    val isAktif = item.status == "aktif" || item.status == "proses"

    val leftBarColor = when {
        isBahaya && isAktif -> RedPrimary
        isBahaya && !isAktif -> GreenTeal
        !isBahaya && isAktif -> OrangePrimary
        else -> GreenTeal
    }

    val bgColor = when {
        isBahaya && isAktif -> RedLight.copy(alpha = 0.4f)
        !isAktif -> GreenLight.copy(alpha = 0.4f)
        else -> OrangeLight.copy(alpha = 0.5f)
    }

    val levelText =
        if (isBahaya) "Bahaya" else "Waspada"

    val statusText = when (item.status) {
        "aktif" -> "Aktif"
        "proses" -> "Proses"
        "selesai" -> "Selesai"
        else -> item.status ?: "-"
    }

    val kategoriLabel = when (item.kategori) {
        "listrik" -> "Listrik"
        "lingkungan" -> "Lingkungan"
        else -> item.kategori ?: "-"
    }

    val kategoriIcon = painterResource(
        id = if (item.kategori == "listrik") {
            R.drawable.baseline_bolt_24
        } else {
            R.drawable.outline_eco_24
        }
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {

        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

            NotifColoredSideBar(color = leftBarColor)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(horizontal = r.listItemPaddingH, vertical = r.listItemPaddingV)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    NotifLevelBadge(
                        levelText = levelText,
                        iconTint = leftBarColor
                    )

                    NotifStatusChip(
                        text = statusText,
                        backgroundColor = leftBarColor,
                        textColor = White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                IconTextRow(
                    painter = kategoriIcon,
                    text = kategoriLabel,
                    tint = TextSecondary,
                    iconSize = r.notifIconSize,
                    fontSize = r.notifCardFontSize
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.message ?: "-",
                    fontSize = r.notifCardFontSize,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                IconTextRow(
                    imageVector = Icons.Default.LocationOn,
                    text = item.lokasi ?: "-",
                    tint = TextSecondary,
                    iconSize = r.notifIconSize,
                    fontSize = r.notifSmallFontSize,
                    spaceWidth = 3.dp
                )

                Spacer(modifier = Modifier.height(3.dp))

                IconTextRow(
                    painter = painterResource(R.drawable.baseline_access_time_24),
                    text = formatTimestamp(item.timestamp),
                    tint = TextSecondary,
                    iconSize = r.notifIconSize,
                    fontSize = r.notifSmallFontSize,
                    spaceWidth = 3.dp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun notifikasiScreenPreview() {
    EnergyMonitoringAppTheme {
        notifikasiScreen()
    }
}