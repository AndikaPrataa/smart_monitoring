package com.example.energymonitoringapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.components.admin.BiayaHariIniCard
import com.example.energymonitoringapp.view.components.admin.PemantauanTabRow
import com.example.energymonitoringapp.view.components.teknisi.LingkunganCard
import com.example.energymonitoringapp.view.components.teknisi.ListrikCard
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: Komponen Pemantauan Sensor
 * Menguji LingkunganCard, ListrikCard, PemantauanTabRow, dan SensorCardItem.
 */
@RunWith(AndroidJUnit4::class)
class PemantauanTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─── PemantauanTabRow ────────────────────────────────────────────────────

    /**
     * TC-PEM-01
     * Verifikasi bahwa tab "Lingkungan" dan "Listrik" tampil di PemantauanTabRow.
     */
    @Test
    fun pemantauanTabRow_kedua_tab_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                PemantauanTabRow(
                    selectedTab   = 0,
                    onTabSelected = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Lingkungan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Listrik").assertIsDisplayed()
    }

    /**
     * TC-PEM-02
     * Verifikasi bahwa klik tab "Listrik" memicu callback dengan index 1.
     */
    @Test
    fun pemantauanTabRow_klikListrik_mengirimIndex1() {
        var tabDipilih = -1

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                PemantauanTabRow(
                    selectedTab   = 0,
                    onTabSelected = { tabDipilih = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Listrik").performClick()
        assert(tabDipilih == 1) { "Tab Listrik seharusnya index 1, diterima: $tabDipilih" }
    }

    /**
     * TC-PEM-03
     * Verifikasi bahwa klik tab "Lingkungan" memicu callback dengan index 0.
     */
    @Test
    fun pemantauanTabRow_klikLingkungan_mengirimIndex0() {
        var tabDipilih = -1

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                PemantauanTabRow(
                    selectedTab   = 1,
                    onTabSelected = { tabDipilih = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Lingkungan").performClick()
        assert(tabDipilih == 0) { "Tab Lingkungan seharusnya index 0, diterima: $tabDipilih" }
    }

    // ─── LingkunganCard ──────────────────────────────────────────────────────

    /**
     * TC-PEM-04
     * Verifikasi bahwa LingkunganCard menampilkan semua label parameter lingkungan.
     */
    @Test
    fun lingkunganCard_semuaLabel_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                LingkunganCard(
                    suhu        = "26.5",
                    kelembapan  = "68",
                    pm25        = "12.3",
                    pm10        = "20.1",
                    gasCo       = "5.0",
                    gasCo2      = "450",
                    tvoc        = "80",
                    cahaya      = "320",
                    kebisingan  = "42"
                )
            }
        }

        composeTestRule.onNodeWithText("Udara PM 2,5").assertIsDisplayed()
        composeTestRule.onNodeWithText("Udara PM 10").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gas CO").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gas CO2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cahaya").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kebisingan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Suhu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kelembapan").assertIsDisplayed()
        composeTestRule.onNodeWithText("TVOC").assertIsDisplayed()
    }

    /**
     * TC-PEM-05
     * Verifikasi bahwa LingkunganCard menampilkan nilai sensor suhu yang diberikan.
     */
    @Test
    fun lingkunganCard_nilaiSuhu_tampilBenar() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                LingkunganCard(suhu = "28.3")
            }
        }

        composeTestRule.onNodeWithText("28.3 °C").assertIsDisplayed()
    }

    /**
     * TC-PEM-06
     * Verifikasi bahwa LingkunganCard menampilkan nilai default "-"
     * ketika parameter tidak diberikan.
     */
    @Test
    fun lingkunganCard_nilaiDefault_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                LingkunganCard()
            }
        }

        // "-" muncul sebagai nilai default
        composeTestRule.onAllNodesWithText("- °C").onFirst().assertIsDisplayed()
    }

    // ─── ListrikCard ─────────────────────────────────────────────────────────

    /**
     * TC-PEM-07
     * Verifikasi bahwa ListrikCard menampilkan label Daya, Arus, dan Tegangan.
     */
    @Test
    fun listrikCard_semuaLabel_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                ListrikCard(
                    daya     = "450 W",
                    arus     = "2.1 A",
                    tegangan = "220 V"
                )
            }
        }

        composeTestRule.onNodeWithText("Daya Listrik").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arus Listrik").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tegangan Listrik").assertIsDisplayed()
    }

    /**
     * TC-PEM-08
     * Verifikasi bahwa ListrikCard menampilkan nilai daya listrik yang diberikan.
     */
    @Test
    fun listrikCard_nilaiDaya_tampilBenar() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                ListrikCard(daya = "512 W", arus = "2.3 A", tegangan = "220 V")
            }
        }

        composeTestRule.onNodeWithText("512 W").assertIsDisplayed()
    }

    // ─── BiayaHariIniCard ────────────────────────────────────────────────────

    /**
     * TC-PEM-09
     * Verifikasi bahwa BiayaHariIniCard menampilkan label dan nilai biaya.
     */
    @Test
    fun biayaHariIniCard_labelDanNilai_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                BiayaHariIniCard(biaya = "Rp 25.400")
            }
        }

        composeTestRule.onNodeWithText("Biaya Hari ini").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rp 25.400").assertIsDisplayed()
    }
}
