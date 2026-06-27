package com.example.energymonitoringapp.view.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energymonitoringapp.view.theme.GreenDark
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.White

@Composable
fun EcoUnilaLogo(modifier: Modifier = Modifier,
                 fontSize: TextUnit = 48.sp ) {
    val text = "EcoUNILA"
    val fontWeight = FontWeight.ExtraBold
    val letterSpacing = 1.sp
    val outlineColor = GreenDark
    val shadowColor = GreenPrimary
    val textColor = White

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing,
            color = shadowColor,
            modifier = Modifier.offset(x = 4.dp, y = 5.dp)
        )

        val outlineOffset = 3.dp
        val diagonalOffset = 2.dp

        // Atas
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = 0.dp, y = -outlineOffset))
        // Bawah
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = 0.dp, y = outlineOffset))
        // Kiri
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = -outlineOffset, y = 0.dp))
        // Kanan
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = outlineOffset, y = 0.dp))
        // Diagonal kiri-atas
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = -diagonalOffset, y = -diagonalOffset))
        // Diagonal kanan-atas
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = diagonalOffset, y = -diagonalOffset))
        // Diagonal kiri-bawah
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = -diagonalOffset, y = diagonalOffset))
        // Diagonal kanan-bawah
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight,
            letterSpacing = letterSpacing, color = outlineColor,
            modifier = Modifier.offset(x = diagonalOffset, y = diagonalOffset))

        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing,
            color = textColor
        )
    }
}