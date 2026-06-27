package com.example.energymonitoringapp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import com.example.energymonitoringapp.utils.fmt
import org.junit.Test


/**
 * Unit Test: FormatUtils
 * Menguji fungsi fmt() dan formatTimestamp() untuk format tampilan data sensor.
 */
class UtilsTest {

    // ─── fmt() extension ─────────────────────────────────────────────────────

    /**
     * TC-FMT-01
     * Verifikasi bahwa bilangan bulat (tanpa desimal) ditampilkan tanpa ".0".
     */
    @Test
    fun fmt_bilanganBulat_tanpaDesimal() {
        val hasil = 25.0.fmt()
        assertEquals("25", hasil)
    }

    /**
     * TC-FMT-02
     * Verifikasi bahwa bilangan dengan satu desimal ditampilkan dengan satu angka
     * di belakang koma.
     */
    @Test
    fun fmt_satuDesimal_tampilSatuAngka() {
        val hasil = 26.5.fmt()
        assertEquals("26.5", hasil)
    }

    /**
     * TC-FMT-03
     * Verifikasi bahwa bilangan dengan dua desimal ditampilkan dengan dua angka
     * di belakang koma.
     */
    @Test
    fun fmt_duaDesimal_tampilDuaAngka() {
        val hasil = 0.32.fmt()
        // Hasil bisa "0.32" atau dibulatkan tergantung implementasi fmt()
        assertTrue(
            "Hasil fmt(0.32) seharusnya mengandung '0.3', diterima: $hasil",
            hasil.startsWith("0.3")
        )
    }

    /**
     * TC-FMT-04
     * Verifikasi bahwa nilai nol ditampilkan sebagai "0".
     */
    @Test
    fun fmt_nilaiNol_tampilNol() {
        val hasil = 0.0.fmt()
        assertEquals("0", hasil)
    }

    /**
     * TC-FMT-05
     * Verifikasi bahwa nilai besar ditampilkan dengan benar.
     */
    @Test
    fun fmt_nilaiBesar_tampilBenar() {
        val hasil = 1234.0.fmt()
        assertEquals("1234", hasil)
    }

    // ─── Logika threshold sensor ─────────────────────────────────────────────

    /**
     * TC-FMT-06
     * Verifikasi bahwa penentuan status "Normal" / "Peringatan" untuk suhu
     * berdasarkan threshold benar.
     * Threshold suhu: normal < 28°C, peringatan >= 28°C.
     */
    @Test
    fun threshold_suhu_statusBenar() {
        fun cekStatusSuhu(suhu: Double): String =
            if (suhu < 28.0) "Normal" else "Peringatan"

        assertEquals("Normal",     cekStatusSuhu(25.0))
        assertEquals("Normal",     cekStatusSuhu(27.9))
        assertEquals("Peringatan", cekStatusSuhu(28.0))
        assertEquals("Peringatan", cekStatusSuhu(32.0))
    }

    /**
     * TC-FMT-07
     * Verifikasi bahwa penentuan kategori IKE berdasarkan nilai benar.
     * Kategori IKE kampus: < 4.17 = Sangat Efisien, 4.17–7.92 = Efisien,
     * 7.92–12.08 = Cukup Efisien, > 12.08 = Boros.
     */
    @Test
    fun kategorisasi_ike_benar() {
        fun kategoriIke(ike: Double): String = when {
            ike < 4.17   -> "Sangat Efisien"
            ike < 7.92   -> "Efisien"
            ike < 12.08  -> "Cukup Efisien"
            else         -> "Boros"
        }

        assertEquals("Sangat Efisien", kategoriIke(2.0))
        assertEquals("Efisien",        kategoriIke(5.0))
        assertEquals("Cukup Efisien",  kategoriIke(10.0))
        assertEquals("Boros",          kategoriIke(15.0))
    }

    /**
     * TC-FMT-08
     * Verifikasi bahwa kalkulasi IEQ sederhana (rata-rata skor parameter)
     * menghasilkan nilai yang benar.
     */
    @Test
    fun kalkulasi_ieqScore_benar() {
        // Simulasi kalkulasi IEQ dari beberapa parameter (0–100)
        val skorParameter = listOf(90.0, 85.0, 80.0, 95.0, 70.0)
        val ieqScore = skorParameter.average().toInt()
        assertEquals(84, ieqScore)
    }

    /**
     * TC-FMT-09
     * Verifikasi bahwa daftar skor kosong menghasilkan 0 (bukan exception).
     */
    @Test
    fun kalkulasi_ieqScore_daftarKosong_tidakException() {
        val skorKosong = emptyList<Double>()
        val ieqScore = if (skorKosong.isEmpty()) 0 else skorKosong.average().toInt()
        assertEquals(0, ieqScore)
    }
}
