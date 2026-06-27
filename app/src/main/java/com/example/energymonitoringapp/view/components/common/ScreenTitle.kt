package com.example.energymonitoringapp.view.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.view.theme.DividerColor
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun ScreenTitle(title: String) {
    val r = rememberResponsiveDimensions()
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = r.fontTitle,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 14.dp)
    )
}

@Composable
fun SectionTitle(title: String) {
    val r = rememberResponsiveDimensions()
    Column {
        Text(
            text = title,
            fontSize = r.fontLabel,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = r.horizontalPadding, vertical = 10.dp)
        )
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
    }
}
