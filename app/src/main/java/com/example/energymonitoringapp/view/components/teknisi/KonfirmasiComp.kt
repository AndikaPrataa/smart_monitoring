package com.example.energymonitoringapp.view.components.teknisi

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.view.components.common.FotoFullScreenViewer
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.GreenDark
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.ImageIconGray
import com.example.energymonitoringapp.view.theme.TextHint
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

@Composable
fun SectionLabel(text: String) {
    val r = rememberResponsiveDimensions()
    Text(
        text       = text,
        fontSize   = r.formLabelSize,
        fontWeight = FontWeight.Medium,
        color      = TextPrimary
    )
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    fieldHeight: Dp? = null
) {
    val r = rememberResponsiveDimensions()
    val modifier = if (fieldHeight != null)
        Modifier.fillMaxWidth().height(fieldHeight)
    else
        Modifier.fillMaxWidth()

    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(text = placeholder, fontSize = r.formHintFontSize, color = TextHint) },
        modifier      = modifier,
        shape         = RoundedCornerShape(10.dp),
        singleLine    = singleLine,
        maxLines      = maxLines,
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = GreenPrimary,
            unfocusedBorderColor    = BorderGray,
            focusedContainerColor   = White,
            unfocusedContainerColor = White
        )
    )
}

@Composable
fun FotoUploadBox(
    fotoUri: Uri?,
    onGalleryClick: () -> Unit
) {
    val r = rememberResponsiveDimensions()
    var showFullScreen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .background(White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) {
                if (fotoUri != null) showFullScreen = true else onGalleryClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (!LocalInspectionMode.current && fotoUri != null) {
            AsyncImage(
                model              = fotoUri,
                contentDescription = "Foto Lapangan",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter            = painterResource(R.drawable.baseline_add_photo_alternate_24),
                    contentDescription = null,
                    tint               = ImageIconGray,
                    modifier           = Modifier.size(r.photoUploadIconSize)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text      = "Ketuk untuk unggah foto",
                    fontSize  = r.photoUploadFontSize,
                    textAlign = TextAlign.Center,
                    color     = TextSecondary
                )
            }
        }
    }

    if (showFullScreen && fotoUri != null) {
        FotoFullScreenViewer(
            fotoUri   = fotoUri,
            onDismiss = { showFullScreen = false }
        )
    }
}

@Composable
fun FotoActionButtons(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val r = rememberResponsiveDimensions()
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick        = onGalleryClick,
            shape          = RoundedCornerShape(10.dp),
            colors         = ButtonDefaults.buttonColors(
                containerColor = GreenDark,
                contentColor   = White
            ),
            contentPadding = PaddingValues(horizontal = r.actionButtonPaddingH, vertical = 10.dp)
        ) {
            Icon(
                painter            = painterResource(R.drawable.baseline_add_to_photos_24),
                contentDescription = null,
                modifier           = Modifier.size(r.actionButtonIconSize)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Galeri", fontSize = r.actionButtonFontSize)
        }

        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "atau", fontSize = r.actionButtonFontSize, color = TextSecondary)
        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick        = onCameraClick,
            shape          = RoundedCornerShape(10.dp),
            colors         = ButtonDefaults.buttonColors(
                containerColor = GreenDark,
                contentColor   = White
            ),
            contentPadding = PaddingValues(horizontal = r.actionButtonPaddingH, vertical = 10.dp)
        ) {
            Icon(
                painter            = painterResource(R.drawable.baseline_add_a_photo_24),
                contentDescription = null,
                modifier           = Modifier.size(r.actionButtonIconSize)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Kamera", fontSize = r.actionButtonFontSize)
        }
    }
}