package com.example.energymonitoringapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energymonitoringapp.view.components.common.LoginForm
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Test: LoginForm (Komponen)
 * Menguji validasi input, interaksi form, dan visibilitas elemen
 * pada komponen LoginForm yang digunakan Admin maupun Teknisi.
 */
@RunWith(AndroidJUnit4::class)
class LoginFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Helper: render LoginForm dengan state yang bisa dikontrol dari test
    @Composable
    private fun RenderLoginForm(
        emailError: String = "",
        passError: String = "",
        onLoginClick: () -> Unit = {}
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var showPassword by remember { mutableStateOf(false) }

        LoginForm(
            title           = "Masuk (Admin)",
            email           = email,
            onEmailChange   = { email = it },
            emailError      = emailError,
            password        = password,
            onPassChange    = { password = it },
            passError       = passError,
            showPassword    = showPassword,
            onTogglePass    = { showPassword = !showPassword },
            onLoginClick    = onLoginClick
        )
    }

    /**
     * TC-LOGIN-01
     * Verifikasi bahwa judul form "Masuk (Admin)" tampil dengan benar.
     */
    @Test
    fun loginForm_judul_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme { RenderLoginForm() }
        }

        composeTestRule.onNodeWithText("Masuk (Admin)").assertIsDisplayed()
    }

    /**
     * TC-LOGIN-02
     * Verifikasi bahwa field Email dan Password tampil.
     */
    @Test
    fun loginForm_fieldEmail_danPassword_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme { RenderLoginForm() }
        }

        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    /**
     * TC-LOGIN-03
     * Verifikasi bahwa tombol "Masuk" tampil dan bisa diklik.
     */
    @Test
    fun loginForm_tombolMasuk_tampilDanBisaDiklik() {
        var diklik = false

        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                RenderLoginForm(onLoginClick = { diklik = true })
            }
        }

        composeTestRule.onNodeWithText("Masuk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masuk").performClick()
        assert(diklik) { "Tombol Masuk tidak memicu callback" }
    }

    /**
     * TC-LOGIN-04
     * Verifikasi bahwa placeholder "Masukkan email anda" tampil saat field kosong.
     */
    @Test
    fun loginForm_placeholder_email_tampil() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme { RenderLoginForm() }
        }

        composeTestRule.onNodeWithText("Masukkan email anda").assertIsDisplayed()
    }

    /**
     * TC-LOGIN-05
     * Verifikasi bahwa pesan error email tampil ketika emailError tidak kosong.
     */
    @Test
    fun loginForm_pesanErrorEmail_tampilJikaAdaError() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                RenderLoginForm(emailError = "Email tidak boleh kosong")
            }
        }

        composeTestRule.onNodeWithText("Email tidak boleh kosong").assertIsDisplayed()
    }

    /**
     * TC-LOGIN-06
     * Verifikasi bahwa pesan error password tampil ketika passError tidak kosong.
     */
    @Test
    fun loginForm_pesanErrorPassword_tampilJikaAdaError() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                RenderLoginForm(passError = "Password tidak boleh kosong")
            }
        }

        composeTestRule.onNodeWithText("Password tidak boleh kosong").assertIsDisplayed()
    }

    /**
     * TC-LOGIN-07
     * Verifikasi bahwa pengguna dapat mengetik di field email.
     */
    @Test
    fun loginForm_inputEmail_dapatDiketik() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme { RenderLoginForm() }
        }

        composeTestRule
            .onNodeWithText("Masukkan email anda")
            .performTextInput("admin@example.com")

        composeTestRule
            .onNodeWithText("admin@example.com")
            .assertIsDisplayed()
    }

    /**
     * TC-LOGIN-08
     * Verifikasi bahwa form menampilkan judul berbeda untuk Teknisi.
     */
    @Test
    fun loginForm_judulTeknisi_tampilBenar() {
        composeTestRule.setContent {
            EnergyMonitoringAppTheme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var showPassword by remember { mutableStateOf(false) }

                LoginForm(
                    title           = "Masuk (Teknisi)",
                    email           = email,
                    onEmailChange   = { email = it },
                    emailError      = "",
                    password        = password,
                    onPassChange    = { password = it },
                    passError       = "",
                    showPassword    = showPassword,
                    onTogglePass    = { showPassword = !showPassword },
                    onLoginClick    = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Masuk (Teknisi)").assertIsDisplayed()
    }
}
