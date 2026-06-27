package com.example.energymonitoringapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.local.NotifEntity
import com.example.energymonitoringapp.data.local.SensorDao
import com.example.energymonitoringapp.data.repository.SensorRepository
import com.example.energymonitoringapp.data.response.AllNotifResponseItem
import com.example.energymonitoringapp.data.response.BiayaRealtime
import com.example.energymonitoringapp.data.response.DayaListrikResponse
import com.example.energymonitoringapp.data.response.KonsumsiEnergiResponse
import com.example.energymonitoringapp.data.response.LingkunganData
import com.example.energymonitoringapp.data.response.LingkunganResponse
import com.example.energymonitoringapp.data.response.ListrikResponse
import com.example.energymonitoringapp.data.response.ListrikTotal
import com.example.energymonitoringapp.data.response.TotalActivePower
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NilaiTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockContext: Context
    private lateinit var mockApi: ApiService
    private lateinit var mockDao: SensorDao
    private lateinit var mockCm: ConnectivityManager
    private lateinit var mockNetwork: Network
    private lateinit var mockCaps: NetworkCapabilities
    private lateinit var repository: SensorRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockContext  = mock()
        mockApi      = mock()
        mockDao      = mock()
        mockCm       = mock()
        mockNetwork  = mock()
        mockCaps     = mock()

        whenever(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm)

        repository = SensorRepository(mockContext, mockApi, mockDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Helper: simulasi koneksi online/offline ───────────────────────────

    private fun simulateOnline() {
        whenever(mockCm.activeNetwork).thenReturn(mockNetwork)
        whenever(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCaps)
        whenever(mockCaps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            .thenReturn(true)
    }

    private fun simulateOffline() {
        whenever(mockCm.activeNetwork).thenReturn(null)
    }

    // ─── TC-SR-01: isOnline true saat ada internet ─────────────────────────

    /**
     * TC-SR-01
     * Verifikasi bahwa isOnline() mengembalikan true ketika perangkat
     * memiliki koneksi internet aktif.
     */
    @Test
    fun `isOnline mengembalikan true saat ada internet`() {
        simulateOnline()

        assertTrue("isOnline seharusnya true", repository.isOnline())
    }

    // ─── TC-SR-02: isOnline false saat offline ─────────────────────────────

    /**
     * TC-SR-02
     * Verifikasi bahwa isOnline() mengembalikan false ketika perangkat
     * tidak memiliki koneksi internet.
     */
    @Test
    fun `isOnline mengembalikan false saat offline`() {
        simulateOffline()

        assertFalse("isOnline seharusnya false", repository.isOnline())
    }

    // ─── TC-SR-03: fetchLingkungan online sukses ───────────────────────────

    /**
     * TC-SR-03
     * Verifikasi bahwa fetchLingkungan() mengembalikan data dan menyimpan
     * ke Room saat online dan API berhasil.
     */
    @Test
    fun `fetchLingkungan online sukses return data dan simpan ke Room`() = runTest {
        simulateOnline()
        val expectedResponse = LingkunganResponse(
            data = LingkunganData(suhu = 28.0, kelembapan = 65.0)
        )
        whenever(mockApi.getMonitoringAll()).thenReturn(Response.success(expectedResponse))

        val result = repository.fetchLingkungan()

        assertNotNull("Hasil seharusnya tidak null", result)
        assertEquals("Data suhu seharusnya 28.0", 28.0, result?.data?.suhu)
        verify(mockDao).saveLingkungan(any())
    }

    // ─── TC-SR-04: fetchLingkungan online API gagal ────────────────────────

    /**
     * TC-SR-04
     * Verifikasi bahwa fetchLingkungan() mengembalikan null ketika API
     * melempar exception.
     */
    @Test
    fun `fetchLingkungan online API gagal return null`() = runTest {
        simulateOnline()
        whenever(mockApi.getMonitoringAll()).thenThrow(RuntimeException("Server error"))

        val result = repository.fetchLingkungan()

        assertNull("Hasil seharusnya null saat API gagal", result)
    }

    // ─── TC-SR-05: fetchLingkungan offline return null ──────────────────────

    /**
     * TC-SR-05
     * Verifikasi bahwa fetchLingkungan() mengembalikan null saat offline
     * dan API tidak dipanggil.
     */
    @Test
    fun `fetchLingkungan offline return null`() = runTest {
        simulateOffline()

        val result = repository.fetchLingkungan()

        assertNull("Hasil seharusnya null saat offline", result)
        verify(mockApi, never()).getMonitoringAll()
    }

    // ─── TC-SR-06: fetchDaya online sukses ─────────────────────────────────

    /**
     * TC-SR-06
     * Verifikasi bahwa fetchDaya() mengembalikan data dan menyimpan ke Room
     * saat online dan API berhasil.
     */
    @Test
    fun `fetchDaya online sukses return dan simpan`() = runTest {
        simulateOnline()
        val expectedResponse = DayaListrikResponse(
            totalActivePower = TotalActivePower(value = 1200.0, unit = "W"),
            biayaRealtime = BiayaRealtime(energyKwh = 8.5, biaya = 12000.0)
        )
        whenever(mockApi.getDayaAll()).thenReturn(Response.success(expectedResponse))

        val result = repository.fetchDaya()

        assertNotNull("Hasil seharusnya tidak null", result)
        assertEquals("Total active power seharusnya 1200.0", 1200.0, result?.totalActivePower?.value)
        verify(mockDao).saveDaya(any())
    }

    // ─── TC-SR-07: fetchListrik online sukses ──────────────────────────────

    /**
     * TC-SR-07
     * Verifikasi bahwa fetchListrik() mengembalikan data dan menyimpan ke Room
     * saat online dan API berhasil.
     */
    @Test
    fun `fetchListrik online sukses return dan simpan`() = runTest {
        simulateOnline()
        val expectedResponse = ListrikResponse(
            total = ListrikTotal(totalVoltage = 380.0, totalCurrent = 20.0),
            timestamp = "2026-06-20T10:00:00"
        )
        whenever(mockApi.getListrikAll()).thenReturn(Response.success(expectedResponse))

        val result = repository.fetchListrik()

        assertNotNull("Hasil seharusnya tidak null", result)
        assertEquals("Total voltage seharusnya 380.0", 380.0, result?.total?.totalVoltage)
        verify(mockDao).saveListrik(any())
    }

    // ─── TC-SR-08: fetchIke online sukses ──────────────────────────────────

    /**
     * TC-SR-08
     * Verifikasi bahwa fetchIke() mengembalikan data saat online
     * dan API berhasil. (IKE tidak disimpan ke Room)
     */
    @Test
    fun `fetchIke online sukses return data`() = runTest {
        simulateOnline()
        val expectedResponse = KonsumsiEnergiResponse(
            ike = 95.5,
            kategori = "Efisien",
            satuan = "kWh/m²/tahun"
        )
        whenever(mockApi.getKonsumsiEnergi()).thenReturn(Response.success(expectedResponse))

        val result = repository.fetchIke()

        assertNotNull("Hasil seharusnya tidak null", result)
        assertEquals("IKE seharusnya 95.5", 95.5, result?.ike)
        assertEquals("Kategori seharusnya Efisien", "Efisien", result?.kategori)
    }

    // ─── TC-SR-09: fetchIke offline return null ────────────────────────────

    /**
     * TC-SR-09
     * Verifikasi bahwa fetchIke() mengembalikan null saat offline.
     */
    @Test
    fun `fetchIke offline return null`() = runTest {
        simulateOffline()

        val result = repository.fetchIke()

        assertNull("Hasil seharusnya null saat offline", result)
    }

    // ─── TC-SR-10: fetchNotif online sukses ────────────────────────────────

    /**
     * TC-SR-10
     * Verifikasi bahwa fetchNotif() menyimpan data ke DAO dan mengembalikan
     * daftar NotifEntity saat online dan API berhasil.
     */
    @Test
    fun `fetchNotif online sukses simpan dan return`() = runTest {
        simulateOnline()
        val apiItems = listOf(
            AllNotifResponseItem(
                id = 1, kategori = "lingkungan", sensor = "suhu",
                title = "Suhu Tinggi", level = "bahaya", value = 40.0,
                unit = "°C", message = "Suhu melebihi batas", lokasi = "Gedung A",
                status = "aktif", timestamp = "2026-06-20T10:00:00"
            ),
            AllNotifResponseItem(
                id = 2, kategori = "listrik", sensor = "arus",
                title = "Arus Lebih", level = "peringatan", value = 25.0,
                unit = "A", message = "Arus melebihi normal", lokasi = "Gedung B",
                status = "aktif", timestamp = "2026-06-20T10:05:00"
            )
        )
        whenever(mockApi.getAllNotifications()).thenReturn(Response.success(apiItems))

        val result = repository.fetchNotif()

        assertEquals("Jumlah notif seharusnya 2", 2, result.size)
        assertEquals("ID notif pertama seharusnya 1", 1, result[0].id)
        assertEquals("Role notif seharusnya 'all'", "all", result[0].role)
        verify(mockDao).saveAllNotif(any())
    }

    // ─── TC-SR-11: fetchNotif offline return dari DAO ──────────────────────

    /**
     * TC-SR-11
     * Verifikasi bahwa fetchNotif() mengembalikan data dari DAO saat offline.
     */
    @Test
    fun `fetchNotif offline return dari DAO`() = runTest {
        simulateOffline()
        val localEntities = listOf(
            NotifEntity(
                id = 10, role = "all", kategori = "lingkungan",
                sensor = "suhu", title = "Cache Notif", level = "peringatan",
                status = "aktif"
            )
        )
        whenever(mockDao.getNotifByRoleOnce("all")).thenReturn(localEntities)

        val result = repository.fetchNotif()

        assertEquals("Jumlah notif dari DAO seharusnya 1", 1, result.size)
        assertEquals("ID notif seharusnya 10", 10, result[0].id)
        verify(mockApi, never()).getAllNotifications()
    }
}
