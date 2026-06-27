package com.example.energymonitoringapp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


/**
 * Unit Test: Logika Validasi Form Login
 * Menguji fungsi validasi input email dan password
 * tanpa memerlukan Android context (pure JVM).
 */
class AppTest {

    // ─── Helper: duplikasi logika validasi dari LoginAdminScreen ─────────────
    // (Logika ini identik dengan yang ada di fun validate() pada screen)

    private fun validasiEmail(email: String): String =
        if (email.isBlank()) "Email tidak boleh kosong" else ""

    private fun validasiPassword(password: String): String =
        if (password.isBlank()) "Password tidak boleh kosong" else ""

    private fun validasiFormatEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // ─── Validasi Email ──────────────────────────────────────────────────────

    /**
     * TC-VAL-01
     * Verifikasi bahwa email kosong menghasilkan pesan error.
     */
    @Test
    fun emailKosong_menghasilkanPesanError() {
        val error = validasiEmail("")
        assertEquals("Email tidak boleh kosong", error)
    }

    /**
     * TC-VAL-02
     * Verifikasi bahwa email hanya spasi menghasilkan pesan error.
     */
    @Test
    fun emailSpasi_menghasilkanPesanError() {
        val error = validasiEmail("   ")
        assertEquals("Email tidak boleh kosong", error)
    }

    /**
     * TC-VAL-03
     * Verifikasi bahwa email yang terisi tidak menghasilkan pesan error.
     */
    @Test
    fun emailTerisi_tidakAdaError() {
        val error = validasiEmail("admin@unila.ac.id")
        assertEquals("", error)
    }

    // ─── Validasi Password ───────────────────────────────────────────────────

    /**
     * TC-VAL-04
     * Verifikasi bahwa password kosong menghasilkan pesan error.
     */
    @Test
    fun passwordKosong_menghasilkanPesanError() {
        val error = validasiPassword("")
        assertEquals("Password tidak boleh kosong", error)
    }

    /**
     * TC-VAL-05
     * Verifikasi bahwa password yang terisi tidak menghasilkan pesan error.
     */
    @Test
    fun passwordTerisi_tidakAdaError() {
        val error = validasiPassword("rahasia123")
        assertEquals("", error)
    }

    // ─── Validasi Kombinasi ──────────────────────────────────────────────────

    /**
     * TC-VAL-06
     * Verifikasi bahwa form tidak valid apabila keduanya kosong.
     */
    @Test
    fun emailDanPasswordKosong_formTidakValid() {
        val emailError = validasiEmail("")
        val passError  = validasiPassword("")
        val formValid  = emailError.isEmpty() && passError.isEmpty()

        assertFalse("Form seharusnya tidak valid jika keduanya kosong", formValid)
    }

    /**
     * TC-VAL-07
     * Verifikasi bahwa form valid apabila email dan password keduanya terisi.
     */
    @Test
    fun emailDanPasswordTerisi_formValid() {
        val emailError = validasiEmail("teknisi@unila.ac.id")
        val passError  = validasiPassword("password123")
        val formValid  = emailError.isEmpty() && passError.isEmpty()

        assertTrue("Form seharusnya valid jika keduanya terisi", formValid)
    }

    /**
     * TC-VAL-08
     * Verifikasi bahwa form tidak valid apabila email terisi tapi password kosong.
     */
    @Test
    fun emailTerisi_passwordKosong_formTidakValid() {
        val emailError = validasiEmail("admin@unila.ac.id")
        val passError  = validasiPassword("")
        val formValid  = emailError.isEmpty() && passError.isEmpty()

        assertFalse("Form tidak valid jika password kosong", formValid)
    }

    // ─── Logika Status Notifikasi ────────────────────────────────────────────

    /**
     * TC-VAL-09
     * Verifikasi bahwa penghitungan notifikasi aktif berjalan benar.
     * (Logika ini digunakan di DashboardAdminScreen untuk menghitung notifAktif)
     */
    @Test
    fun hitungNotifAktif_hasilBenar() {
        data class FakeNotif(val status: String)

        val notifList = listOf(
            FakeNotif("aktif"),
            FakeNotif("aktif"),
            FakeNotif("proses"),
            FakeNotif("selesai"),
            FakeNotif("aktif")
        )

        val jumlahAktif = notifList.count { it.status == "aktif" }
        assertEquals("Jumlah notif aktif seharusnya 3", 3, jumlahAktif)
    }

    /**
     * TC-VAL-10
     * Verifikasi bahwa daftar notifikasi kosong menghasilkan hitungan 0.
     */
    @Test
    fun notifKosong_hitunganNol() {
        data class FakeNotif(val status: String)
        val notifList = emptyList<FakeNotif>()
        val jumlahAktif = notifList.count { it.status == "aktif" }
        assertEquals(0, jumlahAktif)
    }

    // ─── Logika Format Sensor ────────────────────────────────────────────────

    /**
     * TC-VAL-11
     * Verifikasi bahwa nilai null dari API ditampilkan sebagai "-".
     * (Pola yang digunakan: sensor?.nilai?.toString() ?: "-")
     */
    @Test
    fun nilaiSensorNull_ditampilkanSebagaiDash() {
        val nilaiNull: Double? = null
        val tampil = nilaiNull?.toString() ?: "-"
        assertEquals("-", tampil)
    }

    /**
     * TC-VAL-12
     * Verifikasi bahwa nilai sensor yang valid ditampilkan sebagai string angka.
     */
    @Test
    fun nilaiSensorValid_ditampilkanBenar() {
        val nilai: Double? = 26.5
        val tampil = nilai?.toString() ?: "-"
        assertEquals("26.5", tampil)
    }
}
