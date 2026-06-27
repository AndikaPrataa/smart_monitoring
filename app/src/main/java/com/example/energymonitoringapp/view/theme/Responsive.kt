package com.example.energymonitoringapp.view.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Breakpoints (matching Material3 WindowSizeClass thresholds) ───────────
enum class ScreenSize { COMPACT, MEDIUM, EXPANDED }

data class ResponsiveDimensions(
    val screenSize: ScreenSize,
    val screenWidthDp: Dp,

    // Horizontal padding untuk konten utama
    val horizontalPadding: Dp,
    // Padding vertikal section
    val verticalSection: Dp,

    // Typography
    val fontTitle: TextUnit,
    val fontBody: TextUnit,
    val fontLabel: TextUnit,
    val fontCaption: TextUnit,

    // Card & komponen
    val cardPadding: Dp,
    val iconSizeSmall: Dp,
    val iconSizeMedium: Dp,
    val iconSizeLarge: Dp,
    val statValueFontSize: TextUnit,
    val statUnitFontSize: TextUnit,
    val statLabelFontSize: TextUnit,

    // Grid kolom untuk stat cards
    val statGridColumns: Int,

    // Sensor grid kolom
    val sensorGridColumns: Int,

    // Header image size
    val headerImageSize: Dp,

    // Bottom nav
    val navIconSize: Dp,
    val navFontSize: TextUnit,

    // Button
    val buttonHeight: Dp,
    val buttonFontSize: TextUnit,

    // Splash screen
    val splashBannerHeight: Dp,
    val splashLogoSize: Dp,
    val splashTitleFontSize: TextUnit,

    // ─── Tambahan properti responsif ────────────────────────────
    // TopAppBar
    val topBarTitleSize: TextUnit,

    // Screen title (large)
    val screenTitleSize: TextUnit,

    // Section title
    val sectionTitleSize: TextUnit,

    // Dialog
    val dialogIconSize: Dp,
    val dialogTitleSize: TextUnit,
    val dialogBodySize: TextUnit,

    // Notification card
    val notifCardFontSize: TextUnit,
    val notifSmallFontSize: TextUnit,
    val notifIconSize: Dp,
    val notifBadgeIconSize: Dp,
    val notifBadgeFontSize: TextUnit,
    val notifChipFontSize: TextUnit,
    val notifSideBarMinHeight: Dp,

    // Form & input fields
    val formLabelSize: TextUnit,
    val formFieldFontSize: TextUnit,
    val formHintFontSize: TextUnit,

    // Empty state
    val emptyStateIconSize: Dp,
    val emptyStateFontSize: TextUnit,

    // List items
    val listItemPaddingH: Dp,
    val listItemPaddingV: Dp,
    val listItemIconSize: Dp,
    val listItemFontSize: TextUnit,

    // Photo upload
    val photoUploadIconSize: Dp,
    val photoUploadFontSize: TextUnit,

    // Action button icon
    val actionButtonIconSize: Dp,
    val actionButtonFontSize: TextUnit,
    val actionButtonPaddingH: Dp,

    // Content max width for tablet
    val contentMaxWidth: Dp,

    // Button widths
    val buttonWidthSmall: Dp,
    val buttonWidthMedium: Dp,
    val buttonWidthLarge: Dp,

    // Foto preview height
    val fotoPreviewHeight: Dp,
    val fotoEmptyHeight: Dp,
    val fotoEmptyIconSize: Dp,

    // Biaya font size (lebih besar dari fontBody)
    val biayaFontSize: TextUnit,
)

@Composable
fun rememberResponsiveDimensions(): ResponsiveDimensions {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp.dp

    return when {
        // ━━━ SMALL: HP compact/mini (5.0"–6.2", lebar < 360dp) ━━━
        widthDp < 360.dp -> ResponsiveDimensions(
            screenSize = ScreenSize.COMPACT,
            screenWidthDp = widthDp,
            horizontalPadding = 12.dp,
            verticalSection = 8.dp,
            fontTitle = 16.sp,
            fontBody = 13.sp,
            fontLabel = 12.sp,
            fontCaption = 10.sp,
            cardPadding = 10.dp,
            iconSizeSmall = 14.dp,
            iconSizeMedium = 18.dp,
            iconSizeLarge = 22.dp,
            statValueFontSize = 22.sp,
            statUnitFontSize = 12.sp,
            statLabelFontSize = 11.sp,
            statGridColumns = 2,
            sensorGridColumns = 2,
            headerImageSize = 52.dp,
            navIconSize = 20.dp,
            navFontSize = 10.sp,
            buttonHeight = 44.dp,
            buttonFontSize = 14.sp,
            splashBannerHeight = 220.dp,
            splashLogoSize = 110.dp,
            splashTitleFontSize = 28.sp,
            topBarTitleSize = 16.sp,
            screenTitleSize = 19.sp,
            sectionTitleSize = 14.sp,
            dialogIconSize = 34.dp,
            dialogTitleSize = 15.sp,
            dialogBodySize = 13.sp,
            notifCardFontSize = 12.sp,
            notifSmallFontSize = 11.sp,
            notifIconSize = 12.dp,
            notifBadgeIconSize = 16.dp,
            notifBadgeFontSize = 14.sp,
            notifChipFontSize = 11.sp,
            notifSideBarMinHeight = 140.dp,
            formLabelSize = 13.sp,
            formFieldFontSize = 13.sp,
            formHintFontSize = 12.sp,
            emptyStateIconSize = 52.dp,
            emptyStateFontSize = 13.sp,
            listItemPaddingH = 12.dp,
            listItemPaddingV = 8.dp,
            listItemIconSize = 28.dp,
            listItemFontSize = 13.sp,
            photoUploadIconSize = 52.dp,
            photoUploadFontSize = 13.sp,
            actionButtonIconSize = 16.dp,
            actionButtonFontSize = 13.sp,
            actionButtonPaddingH = 14.dp,
            contentMaxWidth = 600.dp,
            buttonWidthSmall = 100.dp,
            buttonWidthMedium = 140.dp,
            buttonWidthLarge = 170.dp,
            fotoPreviewHeight = 170.dp,
            fotoEmptyHeight = 140.dp,
            fotoEmptyIconSize = 30.dp,
            biayaFontSize = 15.sp,
        )
        // ━━━ MEDIUM: HP standar (6.3"–6.9", lebar 360–600dp) ━━━
        // Nilai SAMA PERSIS dengan hardcoded original (Redmi Note 10S = 360dp)
        widthDp < 600.dp -> ResponsiveDimensions(
            screenSize = ScreenSize.COMPACT,
            screenWidthDp = widthDp,
            horizontalPadding = 16.dp,
            verticalSection = 10.dp,
            fontTitle = 18.sp,
            fontBody = 14.sp,
            fontLabel = 13.sp,
            fontCaption = 11.sp,
            cardPadding = 12.dp,
            iconSizeSmall = 16.dp,
            iconSizeMedium = 20.dp,
            iconSizeLarge = 26.dp,
            statValueFontSize = 26.sp,
            statUnitFontSize = 13.sp,
            statLabelFontSize = 12.sp,
            statGridColumns = 2,
            sensorGridColumns = 2,
            headerImageSize = 64.dp,
            navIconSize = 22.dp,
            navFontSize = 11.sp,
            buttonHeight = 50.dp,
            buttonFontSize = 15.sp,
            splashBannerHeight = 280.dp,
            splashLogoSize = 150.dp,
            splashTitleFontSize = 36.sp,
            topBarTitleSize = 18.sp,
            screenTitleSize = 22.sp,
            sectionTitleSize = 16.sp,
            dialogIconSize = 38.dp,
            dialogTitleSize = 16.sp,
            dialogBodySize = 14.sp,
            notifCardFontSize = 13.sp,
            notifSmallFontSize = 12.sp,
            notifIconSize = 14.dp,
            notifBadgeIconSize = 18.dp,
            notifBadgeFontSize = 15.sp,
            notifChipFontSize = 12.sp,
            notifSideBarMinHeight = 165.dp,
            formLabelSize = 14.sp,
            formFieldFontSize = 14.sp,
            formHintFontSize = 13.sp,
            emptyStateIconSize = 64.dp,
            emptyStateFontSize = 15.sp,
            listItemPaddingH = 14.dp,
            listItemPaddingV = 10.dp,
            listItemIconSize = 32.dp,
            listItemFontSize = 14.sp,
            photoUploadIconSize = 64.dp,
            photoUploadFontSize = 14.sp,
            actionButtonIconSize = 18.dp,
            actionButtonFontSize = 14.sp,
            actionButtonPaddingH = 18.dp,
            contentMaxWidth = 600.dp,
            buttonWidthSmall = 120.dp,
            buttonWidthMedium = 160.dp,
            buttonWidthLarge = 200.dp,
            fotoPreviewHeight = 200.dp,
            fotoEmptyHeight = 160.dp,
            fotoEmptyIconSize = 36.dp,
            biayaFontSize = 16.sp,
        )
        // ━━━ TABLET: Tablet/Phablet (7.0"–11"+, lebar 600–840dp) ━━━
        widthDp < 840.dp -> ResponsiveDimensions(
            screenSize = ScreenSize.MEDIUM,
            screenWidthDp = widthDp,
            horizontalPadding = 24.dp,
            verticalSection = 12.dp,
            fontTitle = 20.sp,
            fontBody = 15.sp,
            fontLabel = 14.sp,
            fontCaption = 12.sp,
            cardPadding = 14.dp,
            iconSizeSmall = 18.dp,
            iconSizeMedium = 22.dp,
            iconSizeLarge = 28.dp,
            statValueFontSize = 28.sp,
            statUnitFontSize = 14.sp,
            statLabelFontSize = 13.sp,
            statGridColumns = 4,
            sensorGridColumns = 3,  // 9 sensor / 3 = 3 baris rata
            headerImageSize = 80.dp,
            navIconSize = 24.dp,
            navFontSize = 12.sp,
            buttonHeight = 54.dp,
            buttonFontSize = 16.sp,
            splashBannerHeight = 320.dp,
            splashLogoSize = 180.dp,
            splashTitleFontSize = 42.sp,
            topBarTitleSize = 20.sp,
            screenTitleSize = 22.sp,
            sectionTitleSize = 16.sp,
            dialogIconSize = 44.dp,
            dialogTitleSize = 17.sp,
            dialogBodySize = 15.sp,
            notifCardFontSize = 14.sp,
            notifSmallFontSize = 13.sp,
            notifIconSize = 14.dp,
            notifBadgeIconSize = 20.dp,
            notifBadgeFontSize = 16.sp,
            notifChipFontSize = 13.sp,
            notifSideBarMinHeight = 0.dp,  // Tablet: sidebar mengikuti tinggi konten
            formLabelSize = 15.sp,
            formFieldFontSize = 15.sp,
            formHintFontSize = 14.sp,
            emptyStateIconSize = 72.dp,
            emptyStateFontSize = 16.sp,
            listItemPaddingH = 16.dp,
            listItemPaddingV = 12.dp,
            listItemIconSize = 36.dp,
            listItemFontSize = 15.sp,
            photoUploadIconSize = 72.dp,
            photoUploadFontSize = 15.sp,
            actionButtonIconSize = 20.dp,
            actionButtonFontSize = 15.sp,
            actionButtonPaddingH = 20.dp,
            contentMaxWidth = 680.dp,
            buttonWidthSmall = 140.dp,
            buttonWidthMedium = 180.dp,
            buttonWidthLarge = 220.dp,
            fotoPreviewHeight = 240.dp,
            fotoEmptyHeight = 180.dp,
            fotoEmptyIconSize = 40.dp,
            biayaFontSize = 17.sp,
        )
        // ━━━ TABLET BESAR: Tablet 9"–11"+ (Tab S9 FE ~960dp, lebar ≥ 840dp) ━━━
        else -> ResponsiveDimensions(
            screenSize = ScreenSize.EXPANDED,
            screenWidthDp = widthDp,
            horizontalPadding = 32.dp,
            verticalSection = 16.dp,
            fontTitle = 22.sp,
            fontBody = 16.sp,
            fontLabel = 15.sp,
            fontCaption = 13.sp,
            cardPadding = 16.dp,
            iconSizeSmall = 20.dp,
            iconSizeMedium = 24.dp,
            iconSizeLarge = 32.dp,
            statValueFontSize = 32.sp,
            statUnitFontSize = 15.sp,
            statLabelFontSize = 14.sp,
            statGridColumns = 4,
            sensorGridColumns = 3,  // 9 sensor / 3 = 3 baris rata
            headerImageSize = 96.dp,
            navIconSize = 26.dp,
            navFontSize = 13.sp,
            buttonHeight = 56.dp,
            buttonFontSize = 17.sp,
            splashBannerHeight = 380.dp,
            splashLogoSize = 200.dp,
            splashTitleFontSize = 48.sp,
            topBarTitleSize = 22.sp,
            screenTitleSize = 24.sp,
            sectionTitleSize = 17.sp,
            dialogIconSize = 48.dp,
            dialogTitleSize = 18.sp,
            dialogBodySize = 16.sp,
            notifCardFontSize = 15.sp,
            notifSmallFontSize = 14.sp,
            notifIconSize = 15.dp,
            notifBadgeIconSize = 22.dp,
            notifBadgeFontSize = 17.sp,
            notifChipFontSize = 14.sp,
            notifSideBarMinHeight = 0.dp,  // Tablet: sidebar mengikuti tinggi konten
            formLabelSize = 16.sp,
            formFieldFontSize = 16.sp,
            formHintFontSize = 15.sp,
            emptyStateIconSize = 80.dp,
            emptyStateFontSize = 17.sp,
            listItemPaddingH = 18.dp,
            listItemPaddingV = 14.dp,
            listItemIconSize = 40.dp,
            listItemFontSize = 16.sp,
            photoUploadIconSize = 80.dp,
            photoUploadFontSize = 16.sp,
            actionButtonIconSize = 22.dp,
            actionButtonFontSize = 16.sp,
            actionButtonPaddingH = 22.dp,
            contentMaxWidth = 760.dp,
            buttonWidthSmall = 160.dp,
            buttonWidthMedium = 200.dp,
            buttonWidthLarge = 240.dp,
            fotoPreviewHeight = 280.dp,
            fotoEmptyHeight = 200.dp,
            fotoEmptyIconSize = 44.dp,
            biayaFontSize = 18.sp,
        )
    }
}

val LocalResponsive = compositionLocalOf<ResponsiveDimensions> {
    error("No ResponsiveDimensions provided")
}
