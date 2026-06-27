package com.example.energymonitoringapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.components.common.FilterTabRow
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: FilterTabRow
 * Menguji komponen filter tab yang digunakan pada halaman Notifikasi.
 */
@RunWith(AndroidJUnit4::class)
class FilterTabRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val tabLabels  = listOf("Semua", "Aktif", "Proses", "Selesai")
    private val tabIndices = listOf(0, 1, 2, 3)

    /**
     * TC-FILTER-01
     * Verifikasi bahwa semua tab filter notifikasi tampil.
     */
    @Test
    fun filterTabRow_semuaTab_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                FilterTabRow(
                    tabs         = tabLabels,
                    tabIndices   = tabIndices,
                    selectedTab  = 0,
                    onTabSelected = {}
                )
            }
        }

        tabLabels.forEach { label ->
            composeTestRule.onNodeWithText(label).assertIsDisplayed()
        }
    }

    /**
     * TC-FILTER-02
     * Verifikasi bahwa klik tab "Aktif" memicu callback dengan index 1.
     */
    @Test
    fun filterTabRow_klikAktif_mengirimIndex1() {
        var dipilih = -1

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                FilterTabRow(
                    tabs          = tabLabels,
                    tabIndices    = tabIndices,
                    selectedTab   = 0,
                    onTabSelected = { dipilih = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Aktif").performClick()
        assertEquals("Tab Aktif seharusnya index 1", 1, dipilih)
    }

    /**
     * TC-FILTER-03
     * Verifikasi bahwa klik tab "Selesai" memicu callback dengan index 3.
     */
    @Test
    fun filterTabRow_klikSelesai_mengirimIndex3() {
        var dipilih = -1

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                FilterTabRow(
                    tabs          = tabLabels,
                    tabIndices    = tabIndices,
                    selectedTab   = 0,
                    onTabSelected = { dipilih = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Selesai").performClick()
        assertEquals("Tab Selesai seharusnya index 3", 3, dipilih)
    }

    /**
     * TC-FILTER-04
     * Verifikasi bahwa FilterTabRow berfungsi dengan dua tab saja
     * (kasus PemantauanTabRow: Lingkungan / Listrik).
     */
    @Test
    fun filterTabRow_duaTab_berfungsiNormal() {
        var dipilih = -1

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                FilterTabRow(
                    tabs          = listOf("Lingkungan", "Listrik"),
                    tabIndices    = listOf(0, 1),
                    selectedTab   = 0,
                    onTabSelected = { dipilih = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Listrik").performClick()
        assertEquals(1, dipilih)
    }
}
