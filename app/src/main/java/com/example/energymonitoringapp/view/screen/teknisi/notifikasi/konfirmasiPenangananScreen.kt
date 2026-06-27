package com.example.energymonitoringapp.view.screen.teknisi.notifikasi

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.energymonitoringapp.view.components.common.ConfirmDialog
import com.example.energymonitoringapp.view.components.teknisi.FormTextField
import com.example.energymonitoringapp.view.components.teknisi.FotoActionButtons
import com.example.energymonitoringapp.view.components.teknisi.FotoUploadBox
import com.example.energymonitoringapp.view.components.teknisi.SectionLabel
import com.example.energymonitoringapp.view.theme.BackgroundGray
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.RedPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import com.example.energymonitoringapp.viewmodel.TeknisiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun konfirmasiPenangananScreen(
    notifId: Int = 0,
    onBackClick: () -> Unit = {},
    onKirimSuccess: () -> Unit = {},
    vm: TeknisiViewModel = hiltViewModel()
) {
    val r = rememberResponsiveDimensions()
    val context = LocalContext.current

    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var tindakan by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    var errorTindakan by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val isSubmitting by vm.isSubmitting.collectAsState()
    val konfirmasiSuccess by vm.konfirmasiSuccess.collectAsState()
    val konfirmasiError by vm.konfirmasiError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(konfirmasiSuccess) {
        if (konfirmasiSuccess) {
            vm.resetKonfirmasiState()
            onKirimSuccess()
        }
    }

    LaunchedEffect(konfirmasiError) {
        konfirmasiError?.let { error ->
            snackbarHostState.showSnackbar(error)
            vm.resetKonfirmasiState()
        }
    }

    val cameraUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri.value != Uri.EMPTY) {
            fotoUri = cameraUri.value
            Log.d("FotoUpload", "Camera URI: ${cameraUri.value}")
        } else {
            Log.d("FotoUpload", "Camera failed or URI empty")
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            Log.d("FotoUpload", "Gallery URI: $it")  // ✅ tambahkan
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            cameraUri.value = uri
            cameraLauncher.launch(uri)
        }
    }

    fun bukaKamera() {
        val izinKamera = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        )
        if (izinKamera == PermissionChecker.PERMISSION_GRANTED) {
            val uri = createImageUri(context)
            cameraUri.value = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = RedPrimary,
                    contentColor = White
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Info",
                        fontWeight = FontWeight.Bold,
                        fontSize = r.topBarTitleSize,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = TextPrimary)
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
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(r.iconSizeLarge)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Konfirmasi Penanganan",
                    fontWeight = FontWeight.Bold,
                    fontSize = r.topBarTitleSize,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel(text = "Foto Lapangan")
            Spacer(modifier = Modifier.height(10.dp))

            FotoUploadBox(
                fotoUri = fotoUri,
                onGalleryClick = { galleryLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            FotoActionButtons(
                onGalleryClick = { galleryLauncher.launch("image/*") },
                onCameraClick = { bukaKamera() }
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel(text = "Tindakan yang akan dilakukan")
            Spacer(modifier = Modifier.height(8.dp))
            FormTextField(
                value = tindakan,
                onValueChange = { tindakan = it },
                placeholder = "Tulis tindakan di lapangan ...",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            SectionLabel(text = "Catatan Tambahan")
            Spacer(modifier = Modifier.height(8.dp))
            FormTextField(
                value = catatan,
                onValueChange = { catatan = it },
                placeholder = "Tulis detail kondisi di lapangan ...",
                singleLine = false,
                maxLines = 5,
                fieldHeight = 120.dp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (tindakan.isBlank()) {
                        errorTindakan = true
                    } else {
                        showDialog = true
                    }
                },
                enabled  = !isSubmitting,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(r.buttonWidthMedium)
                    .height(r.buttonHeight),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = White
                )
            ) {
                Text(text = "Kirim", fontWeight = FontWeight.SemiBold, fontSize = r.buttonFontSize)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDialog) {
        ConfirmDialog(
            title       = "Kirim Konfirmasi?",
            message     = "Data penanganan akan dikirim ke sistem.",
            confirmText = "Kirim",
            onConfirm   = {
                showDialog = false
                vm.konfirmasiPenanganan(
                    notifId        = notifId,
                    fotoUri        = fotoUri,
                    actionTaken    = tindakan,
                    additionalNote = catatan
                )
            },
            onDismiss = { showDialog = false }
        )
    }
}

fun createImageUri(context: Context): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "foto_lapangan_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ) ?: Uri.EMPTY
}

@Preview
@Composable
fun konfirmasiPenangananScreenPreview() {
    EnergyMonitoringAppTheme {
        konfirmasiPenangananScreen()
    }
}