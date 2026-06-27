package com.example.energymonitoringapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.components.admin.AdminBottomNavBar
import com.example.energymonitoringapp.view.components.teknisi.TeknisiBottomNavBar
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: Bottom Navigation Bar
 * Menguji navigasi bawah untuk Admin dan Teknisi.
 */
@RunWith(AndroidJUnit4::class)
class BottomNavTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─── AdminBottomNavBar ───────────────────────────────────────────────────

    /**
     * TC-NAV-01
     * Verifikasi bahwa semua item menu Admin tampil di bottom nav.
     */
    @Test
    fun adminNavBar_semuaItem_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminBottomNavBar(
                    selectedTab   = 0,
                    onTabSelected = {},
                    onNavigateTo  = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Beranda").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pemantauan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifikasi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pengaturan").assertIsDisplayed()
    }

    /**
     * TC-NAV-02
     * Verifikasi bahwa klik "Pemantauan" memicu navigasi ke route pemantauan admin.
     */
    @Test
    fun adminNavBar_klikPemantauan_navigasiBenar() {
        var routeTujuan = ""

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminBottomNavBar(
                    selectedTab   = 0,
                    onTabSelected = {},
                    onNavigateTo  = { routeTujuan = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Pemantauan").performClick()
        assert(routeTujuan.contains("pemantauan", ignoreCase = true)) {
            "Route navigasi tidak mengandung 'pemantauan', diterima: $routeTujuan"
        }
    }

    /**
     * TC-NAV-03
     * Verifikasi bahwa klik "Notifikasi" memicu navigasi ke route notifikasi admin.
     */
    @Test
    fun adminNavBar_klikNotifikasi_navigasiBenar() {
        var routeTujuan = ""

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminBottomNavBar(
                    selectedTab   = 0,
                    onTabSelected = {},
                    onNavigateTo  = { routeTujuan = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Notifikasi").performClick()
        assert(routeTujuan.contains("notifikasi", ignoreCase = true)) {
            "Route navigasi tidak mengandung 'notifikasi', diterima: $routeTujuan"
        }
    }

    /**
     * TC-NAV-04
     * Verifikasi bahwa klik "Pengaturan" memicu navigasi ke route pengaturan.
     */
    @Test
    fun adminNavBar_klikPengaturan_navigasiBenar() {
        var routeTujuan = ""

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminBottomNavBar(
                    selectedTab   = 0,
                    onTabSelected = {},
                    onNavigateTo  = { routeTujuan = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Pengaturan").performClick()
        assert(routeTujuan.contains("pengaturan", ignoreCase = true)) {
            "Route navigasi tidak mengandung 'pengaturan', diterima: $routeTujuan"
        }
    }

    /**
     * TC-NAV-05
     * Verifikasi bahwa klik "Beranda" memicu onTabSelected dengan index 0.
     */
    @Test
    fun adminNavBar_klikBeranda_tabIndex0() {
        var tabTerpilih = -1

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                AdminBottomNavBar(
                    selectedTab   = 2,
                    onTabSelected = { tabTerpilih = it },
                    onNavigateTo  = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Beranda").performClick()
        assert(tabTerpilih == 0) { "Tab Beranda seharusnya index 0, diterima: $tabTerpilih" }
    }

    // ─── TeknisiBottomNavBar ─────────────────────────────────────────────────

    /**
     * TC-NAV-06
     * Verifikasi bahwa semua item menu Teknisi tampil di bottom nav.
     */
    @Test
    fun teknisiNavBar_semuaItem_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                TeknisiBottomNavBar(
                    selectedTab   = 0,
                    onTabSelected = {},
                    onNavigateTo  = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Beranda").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifikasi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pengaturan").assertIsDisplayed()
    }

    /**
     * TC-NAV-07
     * Verifikasi bahwa klik "Notifikasi" di nav Teknisi memicu navigasi yang benar.
     */
    @Test
    fun teknisiNavBar_klikNotifikasi_navigasiBenar() {
        var routeTujuan = ""

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                TeknisiBottomNavBar(
                    selectedTab   = 0,
                    onTabSelected = {},
                    onNavigateTo  = { routeTujuan = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Notifikasi").performClick()
        assert(routeTujuan.contains("notifikasi", ignoreCase = true)) {
            "Route navigasi tidak mengandung 'notifikasi', diterima: $routeTujuan"
        }
    }
}
