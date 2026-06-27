package com.example.energymonitoringapp.view.screen.admin.notifikasi

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.data.response.NotifAdminItem
import com.example.energymonitoringapp.utils.formatTimestamp
import com.example.energymonitoringapp.view.components.admin.AdminBottomNavBar
import com.example.energymonitoringapp.view.components.common.FilterTabRow
import com.example.energymonitoringapp.view.components.common.IconTextRow
import com.example.energymonitoringapp.view.components.common.NotifColoredSideBar
import com.example.energymonitoringapp.view.components.common.NotifLevelBadge
import com.example.energymonitoringapp.view.components.common.NotifStatusChip
import com.example.energymonitoringapp.view.components.common.PullToRefreshWrapper
import com.example.energymonitoringapp.view.components.common.ScreenTitle
import com.example.energymonitoringapp.view.theme.BackgroundGray
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
import com.example.energymonitoringapp.viewmodel.AdminViewModel

private fun isDeviceOffline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return true
    val caps = cm.getNetworkCapabilities(network) ?: return true
    return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) }

@Composable
fun notifikasiAdminScreen(
    onNavigateTo: (String) -> Unit = {},
    onNotifClick: (Int) -> Unit = {},
    vm: AdminViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var bottomTab   by remember { mutableIntStateOf(2) }

    val pagingAktif   = vm.pagingAktif.collectAsLazyPagingItems()
    val pagingProses  = vm.pagingProses.collectAsLazyPagingItems()
    val pagingSelesai = vm.pagingSelesai.collectAsLazyPagingItems()

    val scrollAktif   = rememberLazyListState()
    val scrollProses  = rememberLazyListState()
    val scrollSelesai = rememberLazyListState()

    val context       = LocalContext.current
    val r = rememberResponsiveDimensions()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            object : androidx.lifecycle.DefaultLifecycleObserver {
                override fun onResume(owner: androidx.lifecycle.LifecycleOwner) {
                    pagingAktif.refresh()
                    pagingProses.refresh()
                    pagingSelesai.refresh()
                }
            }
        )
    }

    val currentPaging = when (selectedTab) {
        0    -> pagingAktif
        1    -> pagingProses
        else -> pagingSelesai
    }
    val currentScroll = when (selectedTab) {
        0    -> scrollAktif
        1    -> scrollProses
        else -> scrollSelesai
    }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AdminBottomNavBar(
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
                tabs          = listOf("Aktif", "Proses", "Selesai"),
                tabIndices    = listOf(0, 1, 2),
                selectedTab   = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            val offline = isDeviceOffline(context)
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

            Spacer(modifier = Modifier.height(14.dp))

            when (val refresh = currentPaging.loadState.refresh) {
                is LoadState.Loading -> LoadingView()

                is LoadState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "Gagal memuat: ${refresh.error.message}",
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { currentPaging.retry() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }

                else -> {
                    PullToRefreshWrapper(
                        isRefreshing = currentPaging.loadState.refresh is LoadState.Loading,
                        isOffline    = isDeviceOffline(context),
                        onRefresh    = { currentPaging.refresh() }
                    ) {
                        if (currentPaging.itemCount == 0 &&
                            currentPaging.loadState.append.endOfPaginationReached) {
                            EmptyNotifAdminView(selectedTab = selectedTab)
                        } else {
                            LazyColumn(
                                state               = currentScroll,
                                contentPadding      = PaddingValues(horizontal = r.horizontalPadding),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    count = currentPaging.itemCount,
                                    key   = currentPaging.itemKey { it.id ?: 0 }
                                ) { index ->
                                    currentPaging[index]?.let { item ->
                                        AdminNotifCard(
                                            item    = item,
                                            onClick = { item.id?.let(onNotifClick) }
                                        )
                                    }
                                }

                                item {
                                    when (val append = currentPaging.loadState.append) {
                                        is LoadState.Loading -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                        is LoadState.Error -> {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                TextButton(
                                                    onClick = { currentPaging.retry() }
                                                ) { Text("Gagal, tap untuk coba lagi") }
                                            }
                                        }
                                        else -> Spacer(modifier = Modifier.height(8.dp))
                                    }
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
private fun EmptyNotifAdminView(selectedTab: Int) {
    val r = rememberResponsiveDimensions()

    val emptyText = when (selectedTab) {
        0 -> "Tidak ada notifikasi aktif"
        1 -> "Tidak ada notifikasi proses"
        else -> "Tidak ada notifikasi selesai"
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
fun AdminNotifCard(
    item: NotifAdminItem,
    onClick: () -> Unit
) {
    val r = rememberResponsiveDimensions()

    val leftBarColor = when (item.status) {
        "aktif" -> RedPrimary
        "proses" -> OrangePrimary
        "selesai" -> GreenTeal
        else -> BorderGray
    }

    val bgColor = when (item.status) {
        "aktif" -> RedLight.copy(alpha = 0.35f)
        "proses" -> OrangeLight.copy(alpha = 0.5f)
        "selesai" -> GreenLight.copy(alpha = 0.4f)
        else -> White
    }

    val badgeText = when (item.status) {
        "aktif" -> "Aktif"
        "proses" -> "Proses"
        "selesai" -> "Selesai"
        else -> item.status ?: "-"
    }

    val levelText =
        if (item.level == "bahaya") "Bahaya" else "Waspada"

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
                        text = badgeText,
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

@Preview
@Composable
fun notifikasiAdminScreenPreview() {
    EnergyMonitoringAppTheme { notifikasiAdminScreen() }
}
