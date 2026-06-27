package com.example.energymonitoringapp

import androidx.paging.PagingData
import com.example.energymonitoringapp.data.local.NotifEntity
import com.example.energymonitoringapp.data.repository.NotifRepository
import com.example.energymonitoringapp.data.response.AssignTeknisiResponse
import com.example.energymonitoringapp.data.response.DetailNotifAdminResponse
import com.example.energymonitoringapp.data.response.ListTeknisiResponse
import com.example.energymonitoringapp.data.response.NotifAdminItem
import com.example.energymonitoringapp.data.response.TeknisiItem
import com.example.energymonitoringapp.viewmodel.AdminViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
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
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AdminTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockRepo: NotifRepository
    private lateinit var viewModel: AdminViewModel

    @Before
    fun setUp() = kotlinx.coroutines.runBlocking {
        Dispatchers.setMain(testDispatcher)
        mockRepo = mock()

        // Mock init block dependencies: fetchListTeknisi() dipanggil di init
        whenever(mockRepo.fetchListTeknisi()).thenReturn(
            Result.failure(Exception("init"))
        )
        whenever(mockRepo.getLocalTeknisi()).thenReturn(
            flowOf(emptyList())
        )
        whenever(mockRepo.getPagingAdminNotif(any())).thenReturn(
            flowOf(PagingData.empty())
        )

        viewModel = AdminViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── fetchListTeknisi ────────────────────────────────────────────────────

    /**
     * TC-AVM-01
     * Verifikasi bahwa fetchListTeknisi sukses mengisi teknisiList.
     */
    @Test
    fun `fetchListTeknisi sukses mengisi teknisiList`() = runTest {
        val teknisiResponse = ListTeknisiResponse(
            success = true,
            message = "Berhasil",
            total   = 1,
            data    = listOf(
                TeknisiItem(
                    id             = 1,
                    name           = "Budi",
                    email          = "budi@test.com",
                    nip            = "12345",
                    role           = "teknisi",
                    statusTeknisi  = "available",
                    canReceiveTask = true,
                    activeTaskCount = 0
                )
            )
        )
        whenever(mockRepo.fetchListTeknisi()).thenReturn(
            Result.success(Response.success(teknisiResponse))
        )

        viewModel.fetchListTeknisi()

        val result = viewModel.teknisiList.value
        assertNotNull("teknisiList tidak boleh null setelah sukses", result)
        assertEquals("Jumlah teknisi harus 1", 1, result!!.data?.size)
        assertEquals("Nama teknisi harus Budi", "Budi", result.data?.first()?.name)
    }

    /**
     * TC-AVM-02
     * Verifikasi bahwa fetchListTeknisi yang gagal tidak menyebabkan crash.
     * (_errorMessage bersifat private, jadi kita verifikasi VM tetap stabil.)
     */
    @Test
    fun `fetchListTeknisi gagal tidak menyebabkan crash`() = runTest {
        whenever(mockRepo.fetchListTeknisi()).thenReturn(
            Result.failure(Exception("Network error"))
        )

        viewModel.fetchListTeknisi()

        // VM harus tetap stabil — teknisiList tetap null (dari init failure)
        // Tidak ada exception yang dilempar
    }

    // ─── fetchNotifDetail ────────────────────────────────────────────────────

    /**
     * TC-AVM-03
     * Verifikasi bahwa fetchNotifDetail sukses mengisi notifDetail.
     */
    @Test
    fun `fetchNotifDetail sukses mengisi notifDetail`() = runTest {
        val notifItem = NotifAdminItem(
            id       = 10,
            kategori = "suhu",
            sensor   = "DHT22",
            title    = "Suhu Tinggi",
            level    = "warning",
            value    = 40.5,
            unit     = "°C",
            message  = "Suhu melebihi batas",
            lokasi   = "Gedung A",
            status   = "aktif",
            timestamp = "2025-01-01T10:00:00"
        )
        val detailResponse = DetailNotifAdminResponse(
            success = true,
            message = "Berhasil",
            data    = notifItem
        )
        whenever(mockRepo.fetchAdminNotifDetail(10)).thenReturn(
            Result.success(Response.success(detailResponse))
        )

        viewModel.fetchNotifDetail(10)

        val result = viewModel.notifDetail.value
        assertNotNull("notifDetail tidak boleh null", result)
        assertEquals("ID harus 10", 10, result!!.id)
        assertEquals("Title harus sesuai", "Suhu Tinggi", result.title)
    }

    /**
     * TC-AVM-04
     * Verifikasi bahwa fetchNotifDetail API gagal fallback ke data lokal.
     */
    @Test
    fun `fetchNotifDetail API gagal fallback ke lokal`() = runTest {
        val localEntity = NotifEntity(
            id       = 10,
            role     = "admin",
            kategori = "suhu",
            sensor   = "DHT22",
            title    = "Suhu Lokal",
            level    = "warning",
            value    = 38.0,
            unit     = "°C",
            message  = "Data lokal",
            lokasi   = "Gedung B",
            status   = "aktif",
            timestamp = "2025-01-01T09:00:00"
        )
        whenever(mockRepo.fetchAdminNotifDetail(10)).thenReturn(
            Result.failure(Exception("Network error"))
        )
        whenever(mockRepo.getLocalNotifAdminById(10)).thenReturn(localEntity)

        viewModel.fetchNotifDetail(10)

        val result = viewModel.notifDetail.value
        assertNotNull("notifDetail harus terisi dari data lokal", result)
        assertEquals("Title harus dari lokal", "Suhu Lokal", result!!.title)
        assertEquals("Lokasi harus dari lokal", "Gedung B", result.lokasi)
    }

    /**
     * TC-AVM-05
     * Verifikasi bahwa fetchNotifDetail API not successful (response code != 2xx)
     * fallback ke data lokal.
     */
    @Test
    fun `fetchNotifDetail API not successful fallback ke lokal`() = runTest {
        val errorResponse: Response<DetailNotifAdminResponse> = Response.error(
            500,
            "{}".toResponseBody("application/json".toMediaType())
        )
        val localEntity = NotifEntity(
            id       = 5,
            role     = "admin",
            kategori = "listrik",
            sensor   = "PZEM",
            title    = "Tegangan Rendah",
            level    = "danger",
            value    = 180.0,
            unit     = "V",
            message  = "Tegangan di bawah normal",
            lokasi   = "Gedung C",
            status   = "aktif",
            timestamp = "2025-01-01T08:00:00"
        )
        whenever(mockRepo.fetchAdminNotifDetail(5)).thenReturn(
            Result.success(errorResponse)
        )
        whenever(mockRepo.getLocalNotifAdminById(5)).thenReturn(localEntity)

        viewModel.fetchNotifDetail(5)

        val result = viewModel.notifDetail.value
        assertNotNull("notifDetail harus terisi dari lokal saat API not successful", result)
        assertEquals("Title harus dari lokal", "Tegangan Rendah", result!!.title)
    }

    // ─── assignTeknisi ───────────────────────────────────────────────────────

    /**
     * TC-AVM-06
     * Verifikasi bahwa assignTeknisi sukses mengeset assignSuccess = true.
     */
    @Test
    fun `assignTeknisi sukses mengeset assignSuccess`() = runTest {
        val assignResponse = AssignTeknisiResponse(
            success = true,
            message = "Berhasil ditugaskan"
        )
        whenever(mockRepo.assignTeknisi(1, 2)).thenReturn(
            Result.success(Response.success(assignResponse))
        )

        viewModel.assignTeknisi(1, 2)

        assertTrue("assignSuccess harus true", viewModel.assignSuccess.value)
    }

    /**
     * TC-AVM-07
     * Verifikasi bahwa assignTeknisi 422 error menghasilkan pesan teknisi sibuk.
     */
    @Test
    fun `assignTeknisi 422 error teknisi sibuk`() = runTest {
        val errorResponse: Response<AssignTeknisiResponse> = Response.error(
            422,
            "{}".toResponseBody("application/json".toMediaType())
        )
        whenever(mockRepo.assignTeknisi(1, 2)).thenReturn(
            Result.success(errorResponse)
        )

        viewModel.assignTeknisi(1, 2)

        val error = viewModel.assignError.value
        assertNotNull("assignError tidak boleh null", error)
        assertEquals(
            "Pesan error harus sesuai",
            "Teknisi sedang memiliki tugas aktif atau bukan teknisi",
            error
        )
    }

    /**
     * TC-AVM-08
     * Verifikasi bahwa assignTeknisi 404 error menghasilkan pesan tidak ditemukan.
     */
    @Test
    fun `assignTeknisi 404 error tidak ditemukan`() = runTest {
        val errorResponse: Response<AssignTeknisiResponse> = Response.error(
            404,
            "{}".toResponseBody("application/json".toMediaType())
        )
        whenever(mockRepo.assignTeknisi(1, 2)).thenReturn(
            Result.success(errorResponse)
        )

        viewModel.assignTeknisi(1, 2)

        val error = viewModel.assignError.value
        assertNotNull("assignError tidak boleh null", error)
        assertEquals(
            "Pesan error harus sesuai",
            "Notifikasi tidak ditemukan",
            error
        )
    }

    /**
     * TC-AVM-09
     * Verifikasi bahwa assignTeknisi network failure mengeset assignError.
     */
    @Test
    fun `assignTeknisi network failure`() = runTest {
        whenever(mockRepo.assignTeknisi(1, 2)).thenReturn(
            Result.failure(Exception("Koneksi terputus"))
        )

        viewModel.assignTeknisi(1, 2)

        val error = viewModel.assignError.value
        assertNotNull("assignError tidak boleh null saat network failure", error)
        assertTrue(
            "Pesan error harus ada",
            error!!.isNotEmpty()
        )
    }

    // ─── resetAssignState ────────────────────────────────────────────────────

    /**
     * TC-AVM-10
     * Verifikasi bahwa resetAssignState mengembalikan state ke default.
     */
    @Test
    fun `resetAssignState mengembalikan state`() = runTest {
        // Set state terlebih dahulu
        val assignResponse = AssignTeknisiResponse(
            success = true,
            message = "Berhasil"
        )
        whenever(mockRepo.assignTeknisi(1, 2)).thenReturn(
            Result.success(Response.success(assignResponse))
        )
        viewModel.assignTeknisi(1, 2)
        assertTrue("assignSuccess harus true sebelum reset", viewModel.assignSuccess.value)

        // Reset
        viewModel.resetAssignState()

        assertFalse("assignSuccess harus false setelah reset", viewModel.assignSuccess.value)
        assertNull("assignError harus null setelah reset", viewModel.assignError.value)
    }
}
