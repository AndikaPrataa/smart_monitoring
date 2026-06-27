package com.example.energymonitoringapp.view.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun NotifColoredSideBar(color: Color) {
    val r = rememberResponsiveDimensions()
    Box(
        modifier = Modifier
            .width(6.dp)
            .fillMaxHeight()
            .background(
                color = color,
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            )
            .defaultMinSize(minHeight = r.notifSideBarMinHeight)
    )
}

@Composable
fun NotifLevelBadge(
    levelText: String,
    iconTint: Color
) {
    val r = rememberResponsiveDimensions()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(r.notifBadgeIconSize)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = levelText,
            fontWeight = FontWeight.Bold,
            fontSize = r.notifBadgeFontSize,
            color = TextPrimary
        )
    }
}

@Composable
fun NotifStatusChip(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    val r = rememberResponsiveDimensions()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = r.notifChipFontSize,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun IconTextRow(
    text: String,
    tint: Color,
    iconSize: Dp = 14.dp,
    fontSize: TextUnit = 13.sp,
    spaceWidth: Dp = 4.dp,
    imageVector: ImageVector? = null,
    painter: Painter? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when {
            imageVector != null -> Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )

            painter != null -> Icon(
                painter = painter,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.width(spaceWidth))
        Text(text = text, fontSize = fontSize, color = tint)
    }
}