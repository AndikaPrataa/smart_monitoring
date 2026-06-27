package com.example.energymonitoringapp

import android.content.Context
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.local.NotifEntity
import com.example.energymonitoringapp.data.local.SensorDao
import com.example.energymonitoringapp.data.repository.NotifRepository
import com.example.energymonitoringapp.data.response.AssignTeknisiResponse
import com.example.energymonitoringapp.data.response.DetailNotifAdminResponse
import com.example.energymonitoringapp.data.response.DetailNotifTeknisiResponse
import com.example.energymonitoringapp.data.response.KonfirmasiResponse
import com.example.energymonitoringapp.data.response.ListTeknisiResponse
import com.example.energymonitoringapp.data.response.NotifAdminItem
import com.example.energymonitoringapp.data.response.NotifAdminResponse
import com.example.energymonitoringapp.data.response.NotifTeknisiItem
import com.example.energymonitoringapp.data.response.NotifTeknisiResponse
import com.example.energymonitoringapp.data.response.TeknisiItem
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
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
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NotifTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockContext: Context
    private lateinit var mockApi: ApiService
    private lateinit var mockDao: SensorDao
    private lateinit var repository: NotifRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockContext = mock()
        mockApi     = mock()
        mockDao     = mock()
        repository  = NotifRepository(mockContext, mockApi, mockDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── fetchAdminNotif ────────────────────────────────────────────────────

    /**
     * TC-NR-01
     * fetchAdminNotif sukses → data disimpan ke Room via dao.saveAllNotif().
     */
    @Test
    fun `fetchAdminNotif sukses menyimpan ke Room`() = runTest {
        val fakeItem = NotifAdminItem(
            id       = 1,
            title    = "Suhu Tinggi",
            status   = "pending",
            kategori = "suhu",
            sensor   = "DHT22"
        )
        val fakeResponse = NotifAdminResponse(
            success = true,
            message = "OK",
            total   = 1,
            data    = listOf(fakeItem)
        )
        whenever(mockApi.getAdminNotifications(page = 1, perPage = 20))
            .thenReturn(Response.success(fakeResponse))

        val result = repository.fetchAdminNotif()

        assertTrue("Result seharusnya sukses", result.isSuccess)
        verify(mockDao).saveAllNotif(any())
    }

    /**
     * TC-NR-02
     * fetchAdminNotif gagal (exception dari API) → Result.isFailure.
     */
    @Test
    fun `fetchAdminNotif gagal return failure`() = runTest {
        whenever(mockApi.getAdminNotifications(page = 1, perPage = 20))
            .thenThrow(RuntimeException("Connection refused"))

        val result = repository.fetchAdminNotif()

        assertTrue("Result seharusnya failure", result.isFailure)
    }

    // ─── fetchAdminNotifDetail ──────────────────────────────────────────────

    /**
     * TC-NR-03
     * fetchAdminNotifDetail sukses → Result.isSuccess.
     */
    @Test
    fun `fetchAdminNotifDetail sukses`() = runTest {
        val fakeDetail = DetailNotifAdminResponse(
            success = true,
            message = "OK",
            data    = NotifAdminItem(id = 5, title = "Detail Admin")
        )
        whenever(mockApi.getAdminNotificationDetail(5))
            .thenReturn(Response.success(fakeDetail))

        val result = repository.fetchAdminNotifDetail(5)

        assertTrue("Result seharusnya sukses", result.isSuccess)
        val body = result.getOrNull()?.body()
        assertNotNull("Body seharusnya tidak null", body)
        assertEquals("ID seharusnya 5", 5, body!!.data?.id)
    }

    // ─── assignTeknisi ──────────────────────────────────────────────────────

    /**
     * TC-NR-04
     * assignTeknisi sukses → Result.isSuccess.
     */
    @Test
    fun `assignTeknisi sukses`() = runTest {
        val fakeAssignResponse = AssignTeknisiResponse(
            success = true,
            message = "Teknisi berhasil ditugaskan"
        )
        whenever(mockApi.assignTeknisi(eq(1), any()))
            .thenReturn(Response.success(fakeAssignResponse))

        val result = repository.assignTeknisi(notifId = 1, teknisiId = 10)

        assertTrue("Result seharusnya sukses", result.isSuccess)
    }

    // ─── fetchListTeknisi ───────────────────────────────────────────────────

    /**
     * TC-NR-05
     * fetchListTeknisi sukses → data disimpan ke Room via dao.saveAllTeknisi().
     */
    @Test
    fun `fetchListTeknisi sukses menyimpan ke Room`() = runTest {
        val fakeTeknisi = TeknisiItem(
            id              = 1,
            name            = "Budi",
            email           = "budi@example.com",
            role            = "teknisi",
            statusTeknisi   = "available",
            canReceiveTask  = true,
            activeTaskCount = 0
        )
        val fakeResponse = ListTeknisiResponse(
            success = true,
            message = "OK",
            total   = 1,
            data    = listOf(fakeTeknisi)
        )
        whenever(mockApi.getListTeknisi())
            .thenReturn(Response.success(fakeResponse))

        val result = repository.fetchListTeknisi()

        assertTrue("Result seharusnya sukses", result.isSuccess)
        verify(mockDao).saveAllTeknisi(any())
    }

    // ─── fetchTeknisiNotif ──────────────────────────────────────────────────

    /**
     * TC-NR-06
     * fetchTeknisiNotif sukses → data disimpan ke Room via dao.saveAllNotif().
     */
    @Test
    fun `fetchTeknisiNotif sukses menyimpan ke Room`() = runTest {
        val fakeItem = NotifTeknisiItem(
            id       = 3,
            title    = "Tegangan Drop",
            status   = "assigned",
            kategori = "listrik",
            sensor   = "PZEM"
        )
        val fakeResponse = NotifTeknisiResponse(
            success = true,
            message = "OK",
            total   = 1,
            data    = listOf(fakeItem)
        )
        whenever(mockApi.getTeknisiNotifications())
            .thenReturn(Response.success(fakeResponse))

        val result = repository.fetchTeknisiNotif()

        assertTrue("Result seharusnya sukses", result.isSuccess)
        verify(mockDao).saveAllNotif(any())
    }

    // ─── fetchTeknisiNotifDetail ────────────────────────────────────────────

    /**
     * TC-NR-07
     * fetchTeknisiNotifDetail sukses → Result.isSuccess.
     */
    @Test
    fun `fetchTeknisiNotifDetail sukses`() = runTest {
        val fakeDetail = DetailNotifTeknisiResponse(
            success = true,
            message = "OK",
            data    = NotifTeknisiItem(id = 8, title = "Detail Teknisi")
        )
        whenever(mockApi.getTeknisiNotificationDetail(8))
            .thenReturn(Response.success(fakeDetail))

        val result = repository.fetchTeknisiNotifDetail(8)

        assertTrue("Result seharusnya sukses", result.isSuccess)
        val body = result.getOrNull()?.body()
        assertNotNull("Body seharusnya tidak null", body)
        assertEquals("ID seharusnya 8", 8, body!!.data?.id)
    }

    // ─── konfirmasiPenanganan ───────────────────────────────────────────────

    /**
     * TC-NR-08
     * konfirmasiPenanganan tanpa foto (fotoUri = null) → Result.isSuccess,
     * dan fieldPhoto parameter ke API adalah null.
     */
    @Test
    fun `konfirmasiPenanganan sukses`() = runTest {
        val fakeKonfirmasi = KonfirmasiResponse(success = true, message = "Berhasil")
        whenever(mockApi.konfirmasiPenanganan(eq(1), any(), any(), isNull()))
            .thenReturn(Response.success(fakeKonfirmasi))

        val result = repository.konfirmasiPenanganan(
            notifId        = 1,
            fotoUri        = null,
            actionTaken    = "Ganti komponen",
            additionalNote = "Sudah normal"
        )

        assertTrue("Result seharusnya sukses", result.isSuccess)
        verify(mockApi).konfirmasiPenanganan(eq(1), any(), any(), isNull())
    }

    /**
     * TC-NR-09
     * konfirmasiPenanganan network failure → Result.isFailure.
     */
    @Test
    fun `konfirmasiPenanganan network failure`() = runTest {
        whenever(mockApi.konfirmasiPenanganan(eq(2), any(), any(), isNull()))
            .thenThrow(RuntimeException("Socket timeout"))

        val result = repository.konfirmasiPenanganan(
            notifId        = 2,
            fotoUri        = null,
            actionTaken    = "Cek",
            additionalNote = ""
        )

        assertTrue("Result seharusnya failure", result.isFailure)
    }

    // ─── getLocalNotifById ──────────────────────────────────────────────────

    /**
     * TC-NR-10
     * getLocalNotifAdminById → delegasi ke DAO, mengembalikan entity yang benar.
     */
    @Test
    fun `getLocalNotifAdminById delegasi ke DAO`() = runTest {
        val fakeEntity = NotifEntity(
            id     = 42,
            role   = "admin",
            title  = "Admin Lokal",
            status = "pending"
        )
        whenever(mockDao.getNotifByIdAndRole(42, "admin"))
            .thenReturn(fakeEntity)

        val result = repository.getLocalNotifAdminById(42)

        assertNotNull("Entity seharusnya tidak null", result)
        assertEquals("ID seharusnya 42", 42, result!!.id)
        assertEquals("Title seharusnya cocok", "Admin Lokal", result.title)
    }

    /**
     * TC-NR-11
     * getLocalNotifTeknisiById → delegasi ke DAO, mengembalikan entity yang benar.
     */
    @Test
    fun `getLocalNotifTeknisiById delegasi ke DAO`() = runTest {
        val fakeEntity = NotifEntity(
            id     = 77,
            role   = "teknisi",
            title  = "Teknisi Lokal",
            status = "assigned"
        )
        whenever(mockDao.getNotifByIdAndRole(77, "teknisi"))
            .thenReturn(fakeEntity)

        val result = repository.getLocalNotifTeknisiById(77)

        assertNotNull("Entity seharusnya tidak null", result)
        assertEquals("ID seharusnya 77", 77, result!!.id)
        assertEquals("Title seharusnya cocok", "Teknisi Lokal", result.title)
    }
}
