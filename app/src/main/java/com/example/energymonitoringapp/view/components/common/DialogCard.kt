package com.example.energymonitoringapp.view.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.view.theme.DividerColor
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun SuccessDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    dismissible: Boolean = false
) {
    val r = rememberResponsiveDimensions()
    AlertDialog(
        onDismissRequest = { if (dismissible) onConfirm() },
        shape = RoundedCornerShape(16.dp),
        containerColor = White,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(r.dialogIconSize)
            )
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = r.dialogTitleSize,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                fontSize = r.dialogBodySize,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(text = "OK", color = White, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Ya, Konfirmasi",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val r = rememberResponsiveDimensions()
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = White,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(r.dialogIconSize)
            )
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = r.dialogTitleSize,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                fontSize = r.dialogBodySize,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(confirmText, color = White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val r = rememberResponsiveDimensions()
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = White,
        icon = {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(r.dialogIconSize)
            )
        },
        title = {
            Text(
                text = "Keluar Akun",
                fontWeight = FontWeight.Bold,
                fontSize = r.dialogTitleSize,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = "Apakah anda yakin ingin keluar akun?",
                fontSize = r.dialogBodySize,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("OK", color = White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = BorderStroke(
                    1.dp,
                    DividerColor
                )
            ) { Text("Batal") }
        }
    )
}
