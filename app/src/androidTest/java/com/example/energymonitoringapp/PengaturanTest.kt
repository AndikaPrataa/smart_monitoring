package com.example.energymonitoringapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.components.common.MenuRow
import com.example.energymonitoringapp.view.components.common.ProfilCard
import com.example.energymonitoringapp.view.screen.common.pengaturan.MenuItemData
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: Komponen Pengaturan
 * Menguji ProfilCard, MenuRow, dan interaksi menu pengaturan.
 */
@RunWith(AndroidJUnit4::class)
class PengaturanTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * TC-SET-01
     * Verifikasi bahwa ProfilCard menampilkan nama pengguna yang diberikan.
     */
    @Test
    fun profilCard_namaPengguna_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                ProfilCard(
                    nama  = "Budi Santoso",
                    peran = "Admin"
                )
            }
        }

        composeTestRule.onNodeWithText("Budi Santoso").assertIsDisplayed()
    }

    /**
     * TC-SET-02
     * Verifikasi bahwa ProfilCard menampilkan peran pengguna.
     */
    @Test
    fun profilCard_peranPengguna_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                ProfilCard(
                    nama  = "Andi Wijaya",
                    peran = "Teknisi"
                )
            }
        }

        composeTestRule.onNodeWithText("Teknisi").assertIsDisplayed()
    }

    /**
     * TC-SET-03
     * Verifikasi bahwa ProfilCard menampilkan label "Profil".
     */
    @Test
    fun profilCard_labelProfil_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                ProfilCard(nama = "Test User", peran = "Admin")
            }
        }

        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()
    }

    /**
     * TC-SET-04
     * Verifikasi bahwa MenuRow menampilkan label menu yang diberikan.
     */
    @Test
    fun menuRow_label_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                MenuRow(
                    item = MenuItemData(
                        label   = "Edit Profil",
                        icon    = Icons.Default.AccountCircle,
                        route   = "edit_profil"
                    ),
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Edit Profil").assertIsDisplayed()
    }

    /**
     * TC-SET-05
     * Verifikasi bahwa klik MenuRow memicu callback onClick.
     */
    @Test
    fun menuRow_klik_memicuCallback() {
        var diklik = false

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                MenuRow(
                    item = MenuItemData(
                        label   = "Ubah Password",
                        icon    = Icons.Default.Lock,
                        route   = "ubah_password"
                    ),
                    onClick = { diklik = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Ubah Password").performClick()
        assert(diklik) { "Callback onClick tidak dipanggil setelah MenuRow diklik" }
    }

    /**
     * TC-SET-06
     * Verifikasi bahwa beberapa MenuRow dapat ditampilkan bersamaan
     * dan masing-masing bisa diklik secara independen.
     */
    @Test
    fun menuRow_beberapa_diklikSecararIndependen() {
        var diklikEditProfil   = false
        var diklikUbahPassword = false

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                androidx.compose.foundation.layout.Column {
                    MenuRow(
                        item = MenuItemData("Edit Profil", Icons.Default.AccountCircle, "edit_profil"),
                        onClick = { diklikEditProfil = true }
                    )
                    MenuRow(
                        item = MenuItemData("Ubah Password", Icons.Default.Lock, "ubah_password"),
                        onClick = { diklikUbahPassword = true }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Edit Profil").performClick()
        assert(diklikEditProfil) { "Edit Profil tidak diklik" }
        assert(!diklikUbahPassword) { "Ubah Password seharusnya belum diklik" }

        composeTestRule.onNodeWithText("Ubah Password").performClick()
        assert(diklikUbahPassword) { "Ubah Password tidak diklik" }
    }
}
