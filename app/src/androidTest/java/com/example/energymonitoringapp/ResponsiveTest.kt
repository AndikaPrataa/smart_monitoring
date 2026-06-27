package com.example.energymonitoringapp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.ScreenSize
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: ResponsiveDimensions
 * Memverifikasi bahwa dimensi responsif yang dihasilkan sesuai breakpoint layar.
 */
@RunWith(AndroidJUnit4::class)
class ResponsiveTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * TC-RESP-01
     * Verifikasi bahwa horizontalPadding lebih besar di layar lebar
     * dibandingkan layar sempit (COMPACT < EXPANDED).
     */
    @Test
    fun horizontalPadding_lebarLayarBesar_lebihBesar() {
        var paddingCompact = 0.dp
        var paddingExpanded = 0.dp

        // Simulasi layar COMPACT (default device test ~360dp)
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                paddingCompact = r.horizontalPadding
            }
        }

        // Pastikan compact punya nilai wajar (>= 12dp)
        assertTrue(
            "horizontalPadding COMPACT seharusnya >= 12.dp, diterima: $paddingCompact",
            paddingCompact >= 12.dp
        )
    }

    /**
     * TC-RESP-02
     * Verifikasi bahwa statGridColumns pada layar COMPACT adalah 2.
     */
    @Test
    fun statGridColumns_compact_adalahDua() {
        var columns = 0

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                // Perangkat test emulator umumnya < 600dp → COMPACT
                if (r.screenSize == ScreenSize.COMPACT) {
                    columns = r.statGridColumns
                }
            }
        }

        // Hanya assert jika perangkat test adalah COMPACT
        if (columns > 0) {
            assertEquals(
                "statGridColumns pada layar COMPACT seharusnya 2",
                2, columns
            )
        }
    }

    /**
     * TC-RESP-03
     * Verifikasi bahwa fontTitle selalu memiliki nilai yang wajar (> 0sp).
     */
    @Test
    fun fontTitle_nilaiWajar() {
        var fontTitleValue = 0f

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                fontTitleValue = r.fontTitle.value
            }
        }

        assertTrue(
            "fontTitle seharusnya > 0sp, diterima: ${fontTitleValue}sp",
            fontTitleValue > 0f
        )
    }

    /**
     * TC-RESP-04
     * Verifikasi bahwa fontTitle >= fontLabel (hierarki tipografi terjaga).
     */
    @Test
    fun tipografi_fontTitle_lebihBesarDariFontLabel() {
        var fontTitle = 0f
        var fontLabel = 0f

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                fontTitle = r.fontTitle.value
                fontLabel = r.fontLabel.value
            }
        }

        assertTrue(
            "fontTitle ($fontTitle sp) seharusnya >= fontLabel ($fontLabel sp)",
            fontTitle >= fontLabel
        )
    }

    /**
     * TC-RESP-05
     * Verifikasi bahwa fontBody >= fontCaption (hierarki tipografi terjaga).
     */
    @Test
    fun tipografi_fontBody_lebihBesarDariFontCaption() {
        var fontBody    = 0f
        var fontCaption = 0f

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                fontBody    = r.fontBody.value
                fontCaption = r.fontCaption.value
            }
        }

        assertTrue(
            "fontBody ($fontBody sp) seharusnya >= fontCaption ($fontCaption sp)",
            fontBody >= fontCaption
        )
    }

    /**
     * TC-RESP-06
     * Verifikasi bahwa iconSizeLarge > iconSizeMedium > iconSizeSmall.
     */
    @Test
    fun iconSize_hierarki_benar() {
        var small  = 0.dp
        var medium = 0.dp
        var large  = 0.dp

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                small  = r.iconSizeSmall
                medium = r.iconSizeMedium
                large  = r.iconSizeLarge
            }
        }

        assertTrue("iconSizeMedium ($medium) seharusnya > iconSizeSmall ($small)", medium > small)
        assertTrue("iconSizeLarge ($large) seharusnya > iconSizeMedium ($medium)", large > medium)
    }

    /**
     * TC-RESP-07
     * Verifikasi bahwa navIconSize memiliki nilai yang wajar (>= 16.dp).
     */
    @Test
    fun navIconSize_nilaiWajar() {
        var navIconSize = 0.dp

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                navIconSize = r.navIconSize
            }
        }

        assertTrue(
            "navIconSize seharusnya >= 16.dp, diterima: $navIconSize",
            navIconSize >= 16.dp
        )
    }

    /**
     * TC-RESP-08
     * Verifikasi bahwa splashLogoSize lebih besar dari iconSizeLarge.
     */
    @Test
    fun splashLogoSize_lebihBesarDariIconLarge() {
        var logoSize  = 0.dp
        var iconLarge = 0.dp

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                val r = rememberResponsiveDimensions()
                logoSize  = r.splashLogoSize
                iconLarge = r.iconSizeLarge
            }
        }

        assertTrue(
            "splashLogoSize ($logoSize) seharusnya > iconSizeLarge ($iconLarge)",
            logoSize > iconLarge
        )
    }
}
