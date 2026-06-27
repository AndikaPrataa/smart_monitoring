package com.example.energymonitoringapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.screen.common.splash.splashScreen
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: SplashScreen
 * Menguji tampilan dan navigasi pada halaman splash / pemilihan peran.
 */
@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * TC-SPLASH-01
     * Verifikasi bahwa tombol "Admin" dan "Teknisi" tampil di splash screen.
     */
    @Test
    fun splashScreen_kedua_tombol_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                splashScreen()
            }
        }

        composeTestRule.onNodeWithText("Admin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Teknisi").assertIsDisplayed()
    }

    /**
     * TC-SPLASH-02
     * Verifikasi bahwa teks "Masuk sebagai" tampil sebagai label pilihan peran.
     */
    @Test
    fun splashScreen_label_masukSebagai_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                splashScreen()
            }
        }

        composeTestRule.onNodeWithText("Masuk sebagai").assertIsDisplayed()
    }

    /**
     * TC-SPLASH-03
     * Verifikasi bahwa klik tombol "Admin" memicu callback onAdminClick.
     */
    @Test
    fun splashScreen_klikAdmin_memicuCallback() {
        var adminDiklik = false

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                splashScreen(
                    onAdminClick    = { adminDiklik = true },
                    onTeknisiClick  = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Admin").performClick()
        assert(adminDiklik) { "Callback onAdminClick tidak dipanggil setelah tombol Admin diklik" }
    }

    /**
     * TC-SPLASH-04
     * Verifikasi bahwa klik tombol "Teknisi" memicu callback onTeknisiClick.
     */
    @Test
    fun splashScreen_klikTeknisi_memicuCallback() {
        var teknisiDiklik = false

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                splashScreen(
                    onAdminClick   = {},
                    onTeknisiClick = { teknisiDiklik = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Teknisi").performClick()
        assert(teknisiDiklik) { "Callback onTeknisiClick tidak dipanggil setelah tombol Teknisi diklik" }
    }

    /**
     * TC-SPLASH-05
     * Verifikasi bahwa teks tagline aplikasi tampil di splash screen.
     */
    @Test
    fun splashScreen_tagline_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                splashScreen()
            }
        }

        composeTestRule.onNodeWithText("Energi Cerdas untuk Kampus Hijau").assertIsDisplayed()
    }
}
