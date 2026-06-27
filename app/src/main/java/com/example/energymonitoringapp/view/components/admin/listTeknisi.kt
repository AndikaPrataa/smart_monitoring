package com.example.energymonitoringapp.view.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.view.theme.DividerColor
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun DaftarTeknisiCard(teknisiList: List<String>) {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = r.horizontalPadding),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimary)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding + 2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(r.iconSizeMedium)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daftar Teknisi",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = r.notifBadgeFontSize
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    teknisiList.forEachIndexed { index, nama ->
                        TeknisiRow(nama = nama)
                        if (index < teknisiList.lastIndex) {
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
fun TeknisiRow(nama: String) {
    val r = rememberResponsiveDimensions()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = r.listItemPaddingH, vertical = r.listItemPaddingV),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(r.listItemIconSize)
                .clip(CircleShape)
                .background(OrangePrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(r.iconSizeMedium + 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = nama,
            fontSize = r.listItemFontSize,
            color = TextPrimary
        )
    }
}