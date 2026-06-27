package com.example.energymonitoringapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.components.common.AdminStatGrid
import com.example.energymonitoringapp.view.components.common.HeaderCard
import com.example.energymonitoringapp.view.components.common.SectionTitle
import com.example.energymonitoringapp.view.components.common.StatusEnergiGrid
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: Komponen Dashboard
 * Menguji HeaderCard, AdminStatGrid, dan SectionTitle
 * tanpa membutuhkan ViewModel (murni UI).
 */
@RunWith(AndroidJUnit4::class)
class DashboardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─── HeaderCard ─────────────────────────────────────────────────────────

    /**
     * TC-DASH-01
     * Verifikasi bahwa HeaderCard menampilkan salam "Halo Admin"
     * ketika role yang diberikan adalah "admin".
     */
    @Test
    fun headerCard_salamAdmin_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                HeaderCard(
                    role     = "admin",
                    lokasi   = "Ruang Server",
                    lastSync = "2025-06-01 10:00:00"
                )
            }
        }

        composeTestRule.onNodeWithText("Halo Admin").assertIsDisplayed()
    }

    /**
     * TC-DASH-02
     * Verifikasi bahwa HeaderCard menampilkan salam "Halo Teknisi"
     * ketika role yang diberikan adalah "teknisi".
     */
    @Test
    fun headerCard_salamTeknisi_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                HeaderCard(
                    role     = "teknisi",
                    lokasi   = "Gedung FMIPA",
                    lastSync = "2025-06-01 09:00:00"
                )
            }
        }

        composeTestRule.onNodeWithText("Halo Teknisi").assertIsDisplayed()
    }

    /**
     * TC-DASH-03
     * Verifikasi bahwa HeaderCard menampilkan nama lokasi yang diberikan.
     */
    @Test
    fun headerCard_lokasiSensor_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                HeaderCard(
                    role     = "admin",
                    lokasi   = "Gedung Rektorat",
                    lastSync = "-"
                )
            }
        }

        composeTestRule.onNodeWithText("Gedung Rektorat").assertIsDisplayed()
    }

    // ─── AdminStatGrid ───────────────────────────────────────────────────────

    /**
     * TC-DASH-04
     * Verifikasi bahwa semua label kartu statistik admin tampil
     * (Status IEQ, Status IKE, Biaya Realtime, Energi Terpakai).
     */
    @Test
    fun adminStatGrid_semuaLabelKartu_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminStatGrid(
                    ieqScore    = 85,
                    ieqStatus   = "Baik",
                    energyKwh   = "12.4",
                    ikeNilai    = "0.32",
                    ikeKategori = "Efisien",
                    biayaHari   = "Rp 18.200",
                    notifAktif  = 3
                )
            }
        }

        composeTestRule.onNodeWithText("Status IEQ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Status IKE").assertIsDisplayed()
        composeTestRule.onNodeWithText("Biaya Realtime").assertIsDisplayed()
        composeTestRule.onNodeWithText("Energi Terpakai").assertIsDisplayed()
    }

    /**
     * TC-DASH-05
     * Verifikasi bahwa nilai IEQ score ditampilkan dengan benar.
     */
    @Test
    fun adminStatGrid_nilaiIeq_tampilBenar() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminStatGrid(
                    ieqScore    = 90,
                    ieqStatus   = "Sangat Baik",
                    energyKwh   = "10.0",
                    ikeNilai    = "0.28",
                    ikeKategori = "Sangat Efisien",
                    biayaHari   = "Rp 14.000",
                    notifAktif  = 0
                )
            }
        }

        composeTestRule.onNodeWithText("90").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sangat Baik").assertIsDisplayed()
    }

    /**
     * TC-DASH-06
     * Verifikasi bahwa nilai IKE dan kategorinya tampil dengan benar.
     */
    @Test
    fun adminStatGrid_nilaiIke_tampilBenar() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminStatGrid(
                    ieqScore    = 75,
                    ieqStatus   = "Cukup",
                    energyKwh   = "22.5",
                    ikeNilai    = "0.45",
                    ikeKategori = "Boros",
                    biayaHari   = "Rp 32.000",
                    notifAktif  = 5
                )
            }
        }

        composeTestRule.onNodeWithText("0.45").assertIsDisplayed()
        composeTestRule.onNodeWithText("Boros").assertIsDisplayed()
    }

    // ─── SectionTitle ────────────────────────────────────────────────────────

    /**
     * TC-DASH-07
     * Verifikasi bahwa SectionTitle menampilkan teks judul seksi yang diberikan.
     */
    @Test
    fun sectionTitle_teks_tampilBenar() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                SectionTitle(title = "Status Energi Saat Ini")
            }
        }

        composeTestRule.onNodeWithText("Status Energi Saat Ini").assertIsDisplayed()
    }

    // ─── StatusEnergiGrid (Teknisi) ──────────────────────────────────────────

    /**
     * TC-DASH-08
     * Verifikasi bahwa StatusEnergiGrid menampilkan label kartu
     * untuk dashboard Teknisi.
     */
    @Test
    fun statusEnergiGrid_semuaLabel_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                StatusEnergiGrid(
                    ieqScore   = 80,
                    ieqStatus  = "Baik",
                    ikeNilai   = "0.35",
                    ikeKategori = "Efisien",
                    energyKwh  = "15.2",
                    notifAktif = 2
                )
            }
        }

        composeTestRule.onNodeWithText("Status IEQ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Status IKE").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifikasi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Energi Terpakai").assertIsDisplayed()
    }
}
