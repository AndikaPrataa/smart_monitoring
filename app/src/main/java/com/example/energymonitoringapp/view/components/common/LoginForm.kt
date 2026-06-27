package com.example.energymonitoringapp.view.components.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.GreenDark
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.RedError
import com.example.energymonitoringapp.view.theme.TextHint
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun LoginForm(
    title: String,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String,
    password: String,
    onPassChange: (String) -> Unit,
    passError: String,
    showPassword: Boolean,
    onTogglePass: () -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean = false
) {
    val r = rememberResponsiveDimensions()

    Text(
        text = title,
        fontSize = r.fontTitle,
        fontWeight = FontWeight.Bold,
        color = GreenDark
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(text = "Email", fontSize = r.fontLabel, color = TextPrimary)
    Spacer(modifier = Modifier.height(6.dp))

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        placeholder = {
            Text(text = "Masukkan email anda", color = TextHint, fontSize = r.fontLabel)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        singleLine = true,
        isError = emailError.isNotEmpty(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = loginFieldColors(),
        textStyle = LocalTextStyle.current.copy(fontSize = r.fontBody)
    )
    if (emailError.isNotEmpty()) {
        Text(
            text = emailError,
            color = RedError,
            fontSize = r.fontCaption,
            modifier = Modifier.padding(start = 4.dp, top = 3.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = "Password", fontSize = r.fontLabel, color = TextPrimary)
    Spacer(modifier = Modifier.height(6.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPassChange,
        placeholder = {
            Text(text = "Password minimal 6 karakter", color = TextHint, fontSize = r.fontLabel)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        singleLine = true,
        isError = passError.isNotEmpty(),
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onTogglePass) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (showPassword) "Sembunyikan" else "Tampilkan",
                    tint = TextHint
                )
            }
        },
        colors = loginFieldColors(),
        textStyle = LocalTextStyle.current.copy(fontSize = r.fontBody)
    )
    if (passError.isNotEmpty()) {
        Text(
            text = passError,
            color = RedError,
            fontSize = r.fontCaption,
            modifier = Modifier.padding(start = 4.dp, top = 3.dp)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLoginClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(r.buttonHeight),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = White),
        enabled = !isLoading
    ) {
        Text(text = "Masuk", fontSize = r.buttonFontSize, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GreenPrimary,
    unfocusedBorderColor = BorderGray,
    errorBorderColor = RedError,
    focusedContainerColor = BackgroundGray,
    unfocusedContainerColor = BackgroundGray,
    errorContainerColor = BackgroundGray,
    cursorColor = GreenPrimary
)
