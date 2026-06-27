package com.example.energymonitoringapp.view.components.teknisi

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.view.theme.DividerColor
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

data class ParameterItem(
    val label: String,
    val value: String,
    val icon: Painter,
    val iconTint: Color
)

@Composable
fun LingkunganCard(
    suhu: String = "-",
    kelembapan: String = "-",
    pm25: String = "-",
    pm10: String = "-",
    gasCo: String = "-",
    gasCo2: String = "-",
    tvoc: String = "-",
    cahaya: String = "-",
    kebisingan: String = "-"
) {
    val r = rememberResponsiveDimensions()
    val items = listOf(
        ParameterItem("Udara PM 2,5", "$pm25 ug/m³", painterResource(R.drawable.baseline_air_24), Color(0xFF42A5F5)),
        ParameterItem("Udara PM 10", "$pm10 ug/m³", painterResource(R.drawable.baseline_air_24), Color(0xFF42A5F5)),
        ParameterItem("Gas CO", "$gasCo ppm", painterResource(R.drawable.outline_language_us_colemak_24), Color(0xFFEF5350)),
        ParameterItem("Gas CO2", "$gasCo2 ppm", painterResource(R.drawable.outline_co2_24), Color(0xFFEF5350)),
        ParameterItem("Cahaya", "$cahaya lux", painterResource(R.drawable.baseline_wb_incandescent_24), Color(0xFFFFA726)),
        ParameterItem("Kebisingan", "$kebisingan dB", painterResource(R.drawable.baseline_volume_up_24), Color(0xFFAB47BC)),
        ParameterItem("Suhu", "$suhu °C", painterResource(R.drawable.baseline_thermostat_24), Color(0xFF26C6DA)),
        ParameterItem("Kelembapan", "$kelembapan %", painterResource(R.drawable.outline_humidity_percentage_24), Color(0xFFEC407A)),
        ParameterItem("TVOC", "$tvoc ppb", painterResource(R.drawable.outline_temp_preferences_eco_24), Color(0xFF66BB6A)),
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = r.horizontalPadding),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding + 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.outline_eco_24),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(r.iconSizeMedium)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lingkungan",
                    color = White,
                    fontSize = r.fontBody,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    items.forEachIndexed { index, item ->
                        ParameterRow(item = item)
                        if (index < items.lastIndex) {
                            HorizontalDivider(color = DividerColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListrikCard(
    daya: String = "-",
    arus: String = "-",
    tegangan: String = "-"
) {
    val r = rememberResponsiveDimensions()
    val items = listOf(
        "Daya Listrik" to daya,
        "Arus Listrik" to arus,
        "Tegangan Listrik" to tegangan
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = r.horizontalPadding),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimary)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding + 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.baseline_bolt_24),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(r.iconSizeMedium)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Listrik", color = White, fontSize = r.fontBody, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    items.forEachIndexed { index, (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = r.listItemPaddingH, vertical = r.listItemPaddingV),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = label, fontSize = r.fontBody, color = TextPrimary)
                            Text(text = value, fontSize = r.fontBody, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                        if (index < items.lastIndex) {
                            HorizontalDivider(color = DividerColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterRow(item: ParameterItem) {
    val r = rememberResponsiveDimensions()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = r.listItemPaddingH, vertical = r.listItemPaddingV),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            val boxSize = (r.iconSizeMedium.value + 14).dp
            Box(
                modifier = Modifier
                    .size(boxSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(item.iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = item.icon,
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(r.iconSizeMedium)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = item.label,
                fontSize = r.fontBody,
                color = TextPrimary,
                maxLines = 1
            )
        }
        Text(
            text = item.value,
            fontSize = r.fontBody,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
