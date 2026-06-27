package com.example.energymonitoringapp.view.screen.common.pengaturan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.DividerColor
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

data class BatasItem(
    val label: String,
    val nilai: String,
    val icon: Painter? = null,
    val iconTint: Color = Color.Gray
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun batasnilaiParameterScreen(onBackClick: () -> Unit = {}) {
    val r = rememberResponsiveDimensions()

    val lingkunganItems = listOf(
        BatasItem(
            "Udara PM 2,5",
            "25 ug/m3",
            painterResource(R.drawable.baseline_air_24),
            Color(0xFF42A5F5)
        ),
        BatasItem(
            "Udara PM 10",
            "70 ug/m3",
            painterResource(R.drawable.baseline_air_24),
            Color(0xFF42A5F5)
        ),
        BatasItem(
            "Gas CO",
            "24 ppm",
            painterResource(R.drawable.outline_language_us_colemak_24),
            Color(0xFFEF5350)
        ),
        BatasItem(
            "Gas eCO2",
            "1000 ppm",
            painterResource(R.drawable.outline_co2_24),
            Color(0xFFEF5350)
        ),
        BatasItem(
            "Cahaya",
            "100-200 lux",
            painterResource(R.drawable.baseline_wb_incandescent_24),
            Color(0xFFFFA726)
        ),
        BatasItem(
            "Kebisingan",
            "50 dBA",
            painterResource(R.drawable.baseline_volume_up_24),
            Color(0xFFAB47BC)
        ),
        BatasItem(
            "Suhu",
            "20-30 °C",
            painterResource(R.drawable.baseline_thermostat_24),
            Color(0xFF26C6DA)
        ),
        BatasItem(
            "Kelembapan",
            "40-60 %",
            painterResource(R.drawable.outline_humidity_percentage_24),
            Color(0xFFEC407A)
        ),
        BatasItem(
            "TVOC",
            "220 ppb",
            painterResource(R.drawable.outline_temp_preferences_eco_24),
            Color(0xFF66BB6A)
        )
    )

    val listrikItems = listOf(
        BatasItem("Daya Listrik", "245 W"),
        BatasItem("Arus Listrik", "80 A"),
        BatasItem("Tegangan Listrik", "380 V")
    )

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
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

            Text(
                text = "Batas Nilai Parameter",
                fontWeight = FontWeight.Bold,
                fontSize = r.screenTitleSize,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            BatasKategoriCard(
                headerIcon = painterResource(R.drawable.outline_eco_24),
                headerLabel = "Lingkungan",
                headerColor = GreenPrimary,
                items = lingkunganItems,
                showItemIcons = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            BatasKategoriCard(
                headerIcon = painterResource(R.drawable.baseline_bolt_24),
                headerLabel = "Listrik",
                headerColor = OrangePrimary,
                items = listrikItems,
                showItemIcons = false
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BatasKategoriCard(
    headerIcon: Painter,
    headerLabel: String,
    headerColor: Color,
    items: List<BatasItem>,
    showItemIcons: Boolean
) {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = headerColor)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding + 2.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = headerIcon,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(r.iconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = headerLabel,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = r.notifBadgeFontSize
                    )
                }
                Text(
                    text = "Normal",
                    color = White,
                    fontSize = r.formLabelSize,
                    fontWeight = FontWeight.Medium
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    items.forEachIndexed { index, item ->
                        BatasItemRow(item = item, showIcon = showItemIcons)
                        if (index < items.lastIndex) {
                            HorizontalDivider(
                                color = DividerColor,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BatasItemRow(item: BatasItem, showIcon: Boolean) {
    val r = rememberResponsiveDimensions()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = r.listItemPaddingH, vertical = r.listItemPaddingV),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showIcon && item.icon != null) {
                Box(
                    modifier = Modifier
                        .size(r.listItemIconSize)
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
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = item.label,
                fontSize = r.listItemFontSize,
                color = TextPrimary
            )
        }
        Text(
            text = item.nilai,
            fontSize = r.listItemFontSize,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Preview
@Composable
fun batasnilaiParameterScreenPreview() {
    EnergyMonitoringAppTheme {
        batasnilaiParameterScreen()
    }
}
