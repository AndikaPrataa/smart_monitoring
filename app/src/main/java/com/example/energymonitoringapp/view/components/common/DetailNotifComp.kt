package com.example.energymonitoringapp.view.components.common

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.energymonitoringapp.data.response.TeknisiItem
import com.example.energymonitoringapp.view.theme.BorderGray
import com.example.energymonitoringapp.view.theme.DividerColor
import com.example.energymonitoringapp.view.theme.GreenTeal
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.RedPrimary
import com.example.energymonitoringapp.view.theme.TextHint
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions

data class DetailNotifData(
    val level: String,
    val levelColor: Color,
    val status: String,
    val statusColor: Color,
    val waktuRelative: String,
    val kategori: String,
    val parameterNama: String,
    val nilaiSaatIni: String,
    val batasNormal: String,
    val lokasi: String,
    val waktu: String
)

@Composable
fun InfoCard(data: DetailNotifData) {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding + 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                NotifLevelBadge(levelText = data.level, iconTint = data.levelColor)
                NotifStatusChip(text = data.status, backgroundColor = data.statusColor,
                    textColor = White)
            }
            Spacer(modifier = Modifier.height(10.dp))
            IconTextRow(
                imageVector = Icons.Default.DateRange,
                text = data.waktuRelative,
                tint = TextSecondary,
                iconSize = r.notifIconSize,
                fontSize = r.formLabelSize,
                spaceWidth = 8.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            IconTextRow(
                imageVector = if (data.kategori == "Listrik")
                    Icons.Default.Bolt else Icons.Default.Eco,
                text = data.kategori,
                tint = TextSecondary,
                iconSize = r.notifIconSize,
                fontSize = r.formLabelSize,
                spaceWidth = 8.dp
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = data.parameterNama,
                fontWeight = FontWeight.Bold,
                fontSize = r.notifBadgeFontSize,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            ParameterDetailRow(label = "Nilai saat ini", value = data.nilaiSaatIni)
            Spacer(modifier = Modifier.height(6.dp))
            ParameterDetailRow(label = "Batas Normal", value = data.batasNormal)
            Spacer(modifier = Modifier.height(6.dp))
            ParameterDetailRow(label = "Lokasi", value = data.lokasi)
            Spacer(modifier = Modifier.height(6.dp))
            ParameterDetailRow(label = "Waktu", value = data.waktu)
        }
    }
}

@Composable
fun ParameterDetailRow(label: String, value: String) {
    val r = rememberResponsiveDimensions()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = r.formLabelSize, color = TextPrimary)
        Text(
            text = value,
            fontSize = r.formLabelSize,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TeknisiDropdownWithId(
    selectedTeknisi: TeknisiItem?,
    expanded: Boolean,
    isError: Boolean,
    teknisiList: List<TeknisiItem> = emptyList(),
    onExpandClick: () -> Unit,
    onDismiss: () -> Unit,
    onTeknisiSelected: (TeknisiItem) -> Unit
) {
    val r = rememberResponsiveDimensions()
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandClick() },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, if (isError) RedPrimary else BorderGray),
            colors = CardDefaults.outlinedCardColors(containerColor = White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = r.listItemPaddingH, vertical = r.listItemPaddingV),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedTeknisi == null) "Pilihan teknisi"
                    else selectedTeknisi.name ?: "Teknisi",
                    color = if (selectedTeknisi == null) TextHint else TextPrimary,
                    fontSize = r.formFieldFontSize
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .background(White)
        ) {
            if (teknisiList.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Tidak ada teknisi tersedia",
                            fontSize = r.notifCardFontSize,
                            color = TextSecondary
                        )
                    },
                    onClick = { onDismiss() }
                )
            } else {
                teknisiList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(r.iconSizeMedium)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.name ?: "Teknisi",
                                        fontSize = r.formFieldFontSize,
                                        color = TextPrimary
                                    )
                                    if (item.canReceiveTask == false) {
                                        Text(
                                            text = "Sedang sibuk",
                                            fontSize = r.fontCaption,
                                            color = OrangePrimary
                                        )
                                    }
                                }
                            }
                        },
                        onClick = { onTeknisiSelected(item) }
                    )
                    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun StatusProsesCard() {
    val r = rememberResponsiveDimensions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        border = BorderStroke(1.dp, OrangePrimary)
    ) {
        Row(
            modifier = Modifier.padding(r.listItemPaddingH),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(r.iconSizeMedium)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Notifikasi sedang diproses oleh teknisi",
                fontSize = r.notifCardFontSize,
                color = Color(0xFF5D4037)
            )
        }
    }
}

@Composable
fun FotoFullScreenViewer(
    fotoUri: Uri,
    onDismiss: () -> Unit
) {
    BackHandler(enabled = true) { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows  = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model              = fotoUri,
                contentDescription = "Foto Lapangan Full",
                contentScale       = ContentScale.Fit,
                modifier           = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { /* Cegah dismiss saat klik gambar */ }
            )

            IconButton(
                onClick  = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(12.dp)
                    .size(36.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint               = White,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DetailPenyelesaianCard(
    namaTeknisi: String?,
    selesaiPada: String?,
    tindakan: String?,
    catatanTambahan: String?,
    fotoUrl: String?
) {
    val r = rememberResponsiveDimensions()
    var showImagePreview by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(r.cardPadding + 4.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint               = GreenTeal,
                    modifier           = Modifier.size(r.iconSizeMedium + 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Detail Penyelesaian",
                    fontWeight = FontWeight.Bold,
                    fontSize   = r.sectionTitleSize,
                    color      = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            PenyelesaianRow(label = "Nama Teknisi",            value = namaTeknisi     ?: "-")
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 10.dp))

            PenyelesaianRow(label = "Selesai Pada",            value = selesaiPada     ?: "-")
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 10.dp))

            PenyelesaianRow(label = "Tindakan yang dilakukan", value = tindakan        ?: "-")
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 10.dp))

            PenyelesaianRow(label = "Catatan Tambahan",        value = catatanTambahan ?: "Tidak Ada")

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text       = "Foto Lapangan",
                fontWeight = FontWeight.SemiBold,
                fontSize   = r.formLabelSize,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (!fotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model              = fotoUrl,
                    contentDescription = "Foto Lapangan",
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(r.fotoPreviewHeight)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showImagePreview = true },
                    contentScale       = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(r.fotoEmptyHeight)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector        = Icons.Default.Image,
                            contentDescription = null,
                            tint               = Color(0xFFBDBDBD),
                            modifier           = Modifier.size(r.fotoEmptyIconSize)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Tidak ada foto", fontSize = r.notifSmallFontSize, color = Color(0xFFBDBDBD))
                    }
                }
            }
        }
    }

    if (showImagePreview && !fotoUrl.isNullOrBlank()) {
        FotoFullScreenViewer(
            fotoUri   = Uri.parse(fotoUrl),
            onDismiss = { showImagePreview = false }
        )
    }
}

@Composable
fun PenyelesaianRow(label: String, value: String) {
    val r = rememberResponsiveDimensions()
    Column {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = r.formLabelSize, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = r.formLabelSize, color = TextSecondary)
    }
}