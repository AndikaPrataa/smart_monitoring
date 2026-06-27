package com.example.energymonitoringapp.view.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.utils.formatTimestamp
import com.example.energymonitoringapp.view.theme.BluePrimary
import com.example.energymonitoringapp.view.theme.GreenBorder
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.OrangeBorder
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.RedBorder
import com.example.energymonitoringapp.view.theme.RedPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun HeaderCard(
    role: String,
    lokasi: String = "Ruang Server",
    lastSync: String = "-"
) {
    val r = rememberResponsiveDimensions()
    val greeting = if (role == "admin") "Halo Admin" else "Halo Teknisi"
    val syncDisplay = formatTimestamp(lastSync)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(horizontal = r.horizontalPadding, vertical = 18.dp)
    ) {
        Column(modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(end = r.headerImageSize + 12.dp)
        ) {
            Text(
                text = greeting,
                color = White,
                fontSize = r.fontTitle,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Yuk pantau pemakaian energi hari ini!",
                color = White.copy(alpha = 0.9f),
                fontSize = r.fontLabel,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(r.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = lokasi,
                    color = White,
                    fontSize = r.fontLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(r.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sinkronisasi: $syncDisplay",
                    color = White,
                    fontSize = r.fontLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(r.headerImageSize)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (role == "admin") Color(0xFFE3F2FD) else Color(0xFFFFF3E0)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = if (role == "admin") R.drawable.bisnisman else R.drawable.soldier
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(r.headerImageSize * 0.7f)
            )
        }
    }
}

data class StatCardData(
    val title: String,
    val value: String,
    val unit: String,
    val subtitle: String,
    val borderColor: Color,
    val icon: Painter,
    val iconTint: Color
)

@Composable
fun StatusEnergiGrid(
    ieqScore: Int = 0,
    ieqStatus: String = "-",
    ikeNilai: String = "-",
    ikeKategori: String = "-",
    energyKwh: String = "-",
    notifAktif: Int = 0
) {
    val r = rememberResponsiveDimensions()
    val cards = listOf(
        StatCardData(
            "Status IEQ", ieqScore.toString(), "%", ieqStatus,
            GreenBorder, painterResource(R.drawable.outline_eco_24), GreenPrimary
        ),
        StatCardData(
            "Status IKE", ikeNilai, "", ikeKategori,
            OrangeBorder, painterResource(R.drawable.baseline_battery_charging_full_24), OrangePrimary
        ),
        StatCardData(
            "Notifikasi", notifAktif.toString(), "Aktif", "",
            RedBorder, painterResource(R.drawable.baseline_warning_24), RedPrimary
        ),
        StatCardData(
            "Energi Terpakai", energyKwh, "kWh", "",
            OrangeBorder, painterResource(R.drawable.baseline_offline_bolt_24), OrangePrimary
        )
    )

    Column(modifier = Modifier.padding(horizontal = r.horizontalPadding, vertical = r.verticalSection)) {
        if (r.statGridColumns >= 4) {
            // Tablet/besar: 4 kolom dalam 1 baris
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                cards.forEach { card ->
                    StatCard(data = card, modifier = Modifier.weight(1f))
                }
            }
        } else {
            // HP: 2x2 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(data = cards[0], modifier = Modifier.weight(1f))
                StatCard(data = cards[1], modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(data = cards[2], modifier = Modifier.weight(1f))
                StatCard(data = cards[3], modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(data: StatCardData, modifier: Modifier = Modifier) {
    val r = rememberResponsiveDimensions()
    val valueFontSize = when {
        data.value.length > 10 -> (r.statValueFontSize.value - 6).sp
        data.value.length > 6 -> (r.statValueFontSize.value - 3).sp
        else -> r.statValueFontSize
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.5.dp, data.borderColor)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = data.icon,
                    contentDescription = null,
                    tint = data.iconTint,
                    modifier = Modifier.size(r.iconSizeMedium)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = data.title,
                    fontSize = r.statLabelFontSize,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = data.value,
                    fontSize = valueFontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = data.unit,
                    fontSize = r.statUnitFontSize,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            if (data.subtitle.isNotEmpty()) {
                Text(
                    text = data.subtitle,
                    fontSize = r.fontCaption,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

data class AdminStatCard(
    val title: String,
    val value: String,
    val unit: String,
    val subtitle: String,
    val borderColor: Color,
    val icon: Painter,
    val iconTint: Color
)

@Composable
fun AdminStatGrid(
    ieqScore: Int = 0,
    ieqStatus: String = "-",
    energyKwh: String = "-",
    ikeNilai: String = "-",
    ikeKategori: String = "-",
    biayaHari: String = "Rp -",
    notifAktif: Int = 0
) {
    val r = rememberResponsiveDimensions()
    val cards = listOf(
        AdminStatCard(
            "Status IEQ", ieqScore.toString(), "%", ieqStatus,
            GreenPrimary, painterResource(R.drawable.outline_eco_24), GreenPrimary
        ),
        AdminStatCard(
            "Status IKE", ikeNilai, "", ikeKategori,
            OrangePrimary, painterResource(R.drawable.baseline_battery_charging_full_24), OrangePrimary
        ),
        AdminStatCard(
            "Biaya Realtime", biayaHari, "", "",
            BluePrimary, painterResource(R.drawable.baseline_attach_money_24), BluePrimary
        ),
        AdminStatCard(
            "Energi Terpakai", energyKwh, "kWh", "",
            OrangePrimary, painterResource(R.drawable.baseline_offline_bolt_24), OrangePrimary
        )
    )

    Column(modifier = Modifier.padding(horizontal = r.horizontalPadding, vertical = r.verticalSection)) {
        if (r.statGridColumns >= 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                cards.forEach { card ->
                    StatCard(data = card, modifier = Modifier.weight(1f))
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(data = cards[0], modifier = Modifier.weight(1f))
                StatCard(data = cards[1], modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(data = cards[2], modifier = Modifier.weight(1f))
                StatCard(data = cards[3], modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(data: AdminStatCard, modifier: Modifier = Modifier) {
    val r = rememberResponsiveDimensions()
    val valueFontSize = when {
        data.value.length > 10 -> (r.statValueFontSize.value - 6).sp
        data.value.length > 6 -> (r.statValueFontSize.value - 3).sp
        else -> r.statValueFontSize
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.5.dp, data.borderColor)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = data.icon,
                    contentDescription = null,
                    tint = data.iconTint,
                    modifier = Modifier.size(r.iconSizeMedium)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = data.title,
                    fontSize = r.statLabelFontSize,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = data.value,
                    fontSize = valueFontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (data.unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = data.unit,
                        fontSize = r.statUnitFontSize,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
            if (data.subtitle.isNotEmpty()) {
                Text(
                    text = data.subtitle,
                    fontSize = r.fontCaption,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
