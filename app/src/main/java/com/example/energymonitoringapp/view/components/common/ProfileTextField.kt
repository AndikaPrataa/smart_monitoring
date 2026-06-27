package com.example.energymonitoringapp.view.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.DisabledBg
import com.example.energymonitoringapp.view.theme.DisabledText
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.RedError
import com.example.energymonitoringapp.view.theme.TextHint
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val r = rememberResponsiveDimensions()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, fontSize = r.fontBody, color = TextHint) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        enabled = enabled,
        trailingIcon = trailingIcon,
        textStyle = LocalTextStyle.current.copy(fontSize = r.fontBody),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            unfocusedBorderColor = BorderGray,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            disabledBorderColor = BorderGray,
            disabledContainerColor = DisabledBg,
            disabledTextColor = DisabledText,
            disabledPlaceholderColor = TextHint
        )
    )
}

@Composable
fun ProfilInfoRow(label: String, value: String) {
    val r = rememberResponsiveDimensions()
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Text(text = label, fontSize = r.fontLabel, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = r.fontBody, color = TextSecondary)
    }
}

@Composable
fun ProfilFieldLabel(text: String) {
    val r = rememberResponsiveDimensions()
    Text(text = text, fontSize = r.fontLabel, fontWeight = FontWeight.Medium, color = TextPrimary)
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit,
    errorMessage: String
) {
    val r = rememberResponsiveDimensions()
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, fontSize = r.fontBody, color = TextHint) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            isError = errorMessage.isNotEmpty(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = LocalTextStyle.current.copy(fontSize = r.fontBody),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "Sembunyikan" else "Tampilkan",
                        tint = if (errorMessage.isNotEmpty()) RedError else TextSecondary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = BorderGray,
                errorBorderColor = RedError,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                errorContainerColor = White
            )
        )
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = errorMessage, color = RedError, fontSize = r.fontCaption, modifier = Modifier.padding(start = 4.dp))
        }
    }
}
