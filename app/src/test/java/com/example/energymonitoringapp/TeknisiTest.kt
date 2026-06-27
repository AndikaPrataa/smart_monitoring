package com.example.energymonitoringapp

import com.example.energymonitoringapp.data.local.NotifEntity
import com.example.energymonitoringapp.data.repository.NotifRepository
import com.example.energymonitoringapp.data.response.DetailNotifTeknisiResponse
import com.example.energymonitoringapp.data.response.KonfirmasiResponse
import com.example.energymonitoringapp.data.response.NotifTeknisiItem
import com.example.energymonitoringapp.data.response.NotifTeknisiResponse
import com.example.energymonitoringapp.viewmodel.TeknisiViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class TeknisiTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockRepository: NotifRepository
    private lateinit var viewModel: TeknisiViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Helper: menyiapkan mock init-block agar ViewModel bisa dibuat tanpa crash.
     * - fetchTeknisiNotif → Result.failure (diabaikan)
     * - getLocalNotifTeknisi → flowOf(emptyList())
     */
    private suspend fun stubInit() {
        whenever(mockRepository.fetchTeknisiNotif())
            .thenReturn(Result.failure(Exception("init")))
        whenever(mockRepository.getLocalNotifTeknisi())
            .thenReturn(flowOf(emptyList()))
    }

    /** Buat ViewModel setelah init-stub siap. */
    private fun createViewModel(): TeknisiViewModel =
        TeknisiViewModel(mockRepository)

    // ─── fetchNotifications ─────────────────────────────────────────────────

    /**
     * TC-TVM-01
     * fetchNotifications sukses → notifications.value terisi dari body Response.
     */
    @Test
    fun `fetchNotifications sukses mengisi notifications`() = runTest {
        val fakeItem = NotifTeknisiItem(id = 1, title = "Sensor overheat", status = "pending")
        val fakeResponse = NotifTeknisiResponse(
            success = true,
            message = "OK",
            total   = 1,
            data    = listOf(fakeItem)
        )

        stubInit()
        viewModel = createViewModel()

        // Override stub untuk panggilan kedua
        whenever(mockRepository.fetchTeknisiNotif())
            .thenReturn(Result.success(Response.success(fakeResponse)))

        viewModel.fetchNotifications()

        val result = viewModel.notifications.value
        assertNotNull("notifications seharusnya tidak null", result)
        assertEquals("Jumlah data seharusnya 1", 1, result!!.data?.size)
        assertEquals("Title seharusnya cocok", "Sensor overheat", result.data?.first()?.title)
    }

    /**
     * TC-TVM-02
     * fetchNotifications gagal → errorMessage.value terisi.
     */
    @Test
    fun `fetchNotifications gagal mengisi errorMessage`() = runTest {
        stubInit()
        viewModel = createViewModel()

        whenever(mockRepository.fetchTeknisiNotif())
            .thenReturn(Result.failure(Exception("error")))

        viewModel.fetchNotifications()

        assertEquals("errorMessage seharusnya 'error'", "error", viewModel.errorMessage.value)
    }

    // ─── fetchNotifDetail ───────────────────────────────────────────────────

    /**
     * TC-TVM-03
     * fetchNotifDetail sukses → notifDetail.value terisi dari body Response.
     */
    @Test
    fun `fetchNotifDetail sukses mengisi notifDetail`() = runTest {
        stubInit()
        viewModel = createViewModel()

        val fakeDetail = NotifTeknisiItem(id = 5, title = "Detail OK", status = "assigned")
        val detailResponse = DetailNotifTeknisiResponse(
            success = true,
            message = "OK",
            data    = fakeDetail
        )
        whenever(mockRepository.fetchTeknisiNotifDetail(5))
            .thenReturn(Result.success(Response.success(detailResponse)))

        viewModel.fetchNotifDetail(5)

        val detail = viewModel.notifDetail.value
        assertNotNull("notifDetail seharusnya tidak null", detail)
        assertEquals("ID seharusnya 5", 5, detail!!.id)
        assertEquals("Title seharusnya cocok", "Detail OK", detail.title)
    }

    /**
     * TC-TVM-04
     * fetchNotifDetail API gagal → fallback ke data lokal.
     */
    @Test
    fun `fetchNotifDetail API gagal fallback ke lokal`() = runTest {
        stubInit()
        viewModel = createViewModel()

        whenever(mockRepository.fetchTeknisiNotifDetail(10))
            .thenReturn(Result.failure(Exception("network error")))

        val localEntity = NotifEntity(
            id       = 10,
            role     = "teknisi",
            title    = "Lokal Title",
            status   = "pending",
            kategori = "suhu",
            sensor   = "DHT22",
            level    = "warning",
            value    = 45.0,
            unit     = "°C",
            message  = "Suhu tinggi",
            lokasi   = "Gedung A"
        )
        whenever(mockRepository.getLocalNotifTeknisiById(10))
            .thenReturn(localEntity)

        viewModel.fetchNotifDetail(10)

        val detail = viewModel.notifDetail.value
        assertNotNull("notifDetail seharusnya terisi dari lokal", detail)
        assertEquals("ID seharusnya 10", 10, detail!!.id)
        assertEquals("Title seharusnya dari lokal", "Lokal Title", detail.title)
    }

    /**
     * TC-TVM-05
     * fetchNotifDetail API not successful (misal 500) → fallback ke data lokal.
     */
    @Test
    fun `fetchNotifDetail API not successful fallback ke lokal`() = runTest {
        stubInit()
        viewModel = createViewModel()

        val errorBody = "{}".toResponseBody("application/json".toMediaType())
        whenever(mockRepository.fetchTeknisiNotifDetail(7))
            .thenReturn(Result.success(Response.error(500, errorBody)))

        val localEntity = NotifEntity(
            id     = 7,
            role   = "teknisi",
            title  = "Fallback Title",
            status = "assigned"
        )
        whenever(mockRepository.getLocalNotifTeknisiById(7))
            .thenReturn(localEntity)

        viewModel.fetchNotifDetail(7)

        val detail = viewModel.notifDetail.value
        assertNotNull("notifDetail seharusnya terisi dari fallback lokal", detail)
        assertEquals("ID seharusnya 7", 7, detail!!.id)
        assertEquals("Title seharusnya dari lokal", "Fallback Title", detail.title)
    }

    // ─── konfirmasiPenanganan ───────────────────────────────────────────────

    /**
     * TC-TVM-06
     * konfirmasiPenanganan sukses → konfirmasiSuccess = true.
     */
    @Test
    fun `konfirmasiPenanganan sukses`() = runTest {
        stubInit()
        viewModel = createViewModel()

        val fakeKonfirmasi = KonfirmasiResponse(success = true, message = "Berhasil")
        whenever(mockRepository.konfirmasiPenanganan(eq(1), anyOrNull(), any(), any()))
            .thenReturn(Result.success(Response.success(fakeKonfirmasi)))

        // fetchNotifications dipanggil lagi setelah sukses, stub supaya tidak crash
        whenever(mockRepository.fetchTeknisiNotif())
            .thenReturn(Result.failure(Exception("refresh")))

        viewModel.konfirmasiPenanganan(
            notifId        = 1,
            fotoUri        = null,
            actionTaken    = "Ganti sensor",
            additionalNote = "Sudah diganti"
        )

        assertTrue("konfirmasiSuccess seharusnya true", viewModel.konfirmasiSuccess.value)
    }

    /**
     * TC-TVM-07
     * konfirmasiPenanganan response 422 → konfirmasiError = "Tugas ini sudah diselesaikan sebelumnya".
     */
    @Test
    fun `konfirmasiPenanganan 422 sudah diselesaikan`() = runTest {
        stubInit()
        viewModel = createViewModel()

        val errorBody = "{}".toResponseBody("application/json".toMediaType())
        whenever(mockRepository.konfirmasiPenanganan(eq(2), anyOrNull(), any(), any()))
            .thenReturn(Result.success(Response.error(422, errorBody)))

        viewModel.konfirmasiPenanganan(
            notifId        = 2,
            fotoUri        = null,
            actionTaken    = "Cek",
            additionalNote = ""
        )

        assertEquals(
            "konfirmasiError seharusnya pesan 422",
            "Tugas ini sudah diselesaikan sebelumnya",
            viewModel.konfirmasiError.value
        )
    }

    /**
     * TC-TVM-08
     * konfirmasiPenanganan response 404 → konfirmasiError = "Tugas tidak ditemukan".
     */
    @Test
    fun `konfirmasiPenanganan 404 tidak ditemukan`() = runTest {
        stubInit()
        viewModel = createViewModel()

        val errorBody = "{}".toResponseBody("application/json".toMediaType())
        whenever(mockRepository.konfirmasiPenanganan(eq(99), anyOrNull(), any(), any()))
            .thenReturn(Result.success(Response.error(404, errorBody)))

        viewModel.konfirmasiPenanganan(
            notifId        = 99,
            fotoUri        = null,
            actionTaken    = "Cek",
            additionalNote = ""
        )

        assertEquals(
            "konfirmasiError seharusnya pesan 404",
            "Tugas tidak ditemukan",
            viewModel.konfirmasiError.value
        )
    }

    /**
     * TC-TVM-09
     * konfirmasiPenanganan network failure → konfirmasiError terisi pesan exception.
     */
    @Test
    fun `konfirmasiPenanganan network failure`() = runTest {
        stubInit()
        viewModel = createViewModel()

        whenever(mockRepository.konfirmasiPenanganan(eq(3), anyOrNull(), any(), any()))
            .thenReturn(Result.failure(Exception("timeout")))

        viewModel.konfirmasiPenanganan(
            notifId        = 3,
            fotoUri        = null,
            actionTaken    = "Cek",
            additionalNote = ""
        )

        assertNotNull("konfirmasiError seharusnya tidak null", viewModel.konfirmasiError.value)
        assertTrue(
            "konfirmasiError seharusnya mengandung 'timeout'",
            viewModel.konfirmasiError.value!!.contains("timeout")
        )
    }

    // ─── clearError ─────────────────────────────────────────────────────────

    /**
     * TC-TVM-11
     * clearError mengosongkan errorMessage.
     */
    @Test
    fun `clearError mengosongkan errorMessage`() = runTest {
        stubInit()
        viewModel = createViewModel()

        // Picu error
        whenever(mockRepository.fetchTeknisiNotif())
            .thenReturn(Result.failure(Exception("some error")))
        viewModel.fetchNotifications()
        assertNotNull("errorMessage harus ada sebelum clear", viewModel.errorMessage.value)

        // Clear
        viewModel.clearError()

        assertNull("errorMessage harus null setelah clearError", viewModel.errorMessage.value)
    }
}
