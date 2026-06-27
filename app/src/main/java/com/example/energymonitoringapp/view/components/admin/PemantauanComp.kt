package com.example.energymonitoringapp.view.components.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.view.theme.BlueLight
import com.example.energymonitoringapp.view.theme.BluePrimary
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.GreenDark
import com.example.energymonitoringapp.view.theme.GreenLight
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun StatBanner(
    icon: Painter,
    label: String,
    backgroundColor: Color,
    nilaiText: String,
    satuanText: String,
    statusText: String,
    sejajarSatuan: Boolean = false
) {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = r.horizontalPadding, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(r.iconSizeLarge)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = r.fontLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                val valueFontSize = when {
                    nilaiText.length > 10 -> (r.statValueFontSize.value - 4).sp
                    nilaiText.length > 7 -> (r.statValueFontSize.value - 2).sp
                    else -> r.statValueFontSize
                }
                if (sejajarSatuan) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = nilaiText, color = White, fontWeight = FontWeight.ExtraBold, fontSize = valueFontSize)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = satuanText, color = White, fontSize = r.fontCaption, modifier = Modifier.padding(bottom = 4.dp))
                    }
                } else {
                    Text(text = nilaiText, color = White, fontWeight = FontWeight.ExtraBold, fontSize = valueFontSize)
                    Text(text = satuanText, color = White, fontSize = r.fontCaption, fontWeight = FontWeight.Medium)
                }
                if (statusText.isNotEmpty()) {
                    Text(text = statusText, color = White.copy(alpha = 0.9f), fontSize = r.fontLabel)
                }
            }
        }
    }
}

data class SensorCard(
    val label: String,
    val value: String,
    val unit: String,
    val status: String,
    val icon: Int,
    val iconTint: Color,
    val iconBg: Color,
    val statusColor: Color = GreenDark
)

@Composable
fun SensorCardItem(card: SensorCard, modifier: Modifier = Modifier) {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, card.iconTint)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val boxSize = (r.iconSizeMedium.value + 10).dp
                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .clip(RoundedCornerShape(6.dp))
                        .background(card.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = card.icon),
                        contentDescription = null,
                        tint = card.iconTint,
                        modifier = Modifier.size(r.iconSizeMedium)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = card.label,
                    fontSize = r.statLabelFontSize,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            val valueFontSize = when {
                card.value.length > 8 -> (r.statValueFontSize.value - 4).sp
                else -> r.statValueFontSize
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = card.value,
                    fontSize = valueFontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = card.unit,
                    fontSize = r.statUnitFontSize,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Text(
                text = card.status,
                fontSize = r.fontCaption,
                color = card.statusColor,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ListrikMiniCard(
    label: String,
    value: String,
    unit: String,
    status: String,
    modifier: Modifier = Modifier
) {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.5.dp, OrangePrimary)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding)) {
            Text(text = label, fontSize = r.fontCaption, color = TextSecondary)
            Spacer(modifier = Modifier.height(5.dp))
            val valueFontSize = when {
                value.length > 8 -> (r.statValueFontSize.value - 6).sp
                else -> (r.statValueFontSize.value - 4).sp
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = valueFontSize,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = unit,
                    fontSize = r.fontCaption,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            Text(text = status, fontSize = r.fontCaption, color = GreenDark, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BiayaHariIniCard(biaya: String = "Rp 0") {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = r.horizontalPadding, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val boxSize = (r.iconSizeMedium.value + 12).dp
                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_attach_money_24),
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(r.iconSizeMedium)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Biaya Hari ini",
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = r.fontLabel
                )
            }
            Text(
                text = biaya,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = r.biayaFontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PemantauanTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val r = rememberResponsiveDimensions()
    val tabs = listOf("Lingkungan", "Listrik")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = r.horizontalPadding)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, GreenPrimary, RoundedCornerShape(8.dp))
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (isSelected) GreenLight else White)
                    .clickable(interactionSource = interactionSource, indication = null) { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) GreenDark else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = r.fontLabel
                )
            }
        }
    }
}
