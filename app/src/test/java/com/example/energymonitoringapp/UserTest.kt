package com.example.energymonitoringapp

import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.local.UserProfileDao
import com.example.energymonitoringapp.data.local.UserProfileEntity
import com.example.energymonitoringapp.data.repository.UserRepository
import com.example.energymonitoringapp.data.response.ChangePasswordResponse
import com.example.energymonitoringapp.data.response.UpdateProfileResponse
import com.example.energymonitoringapp.data.response.UserData
import com.example.energymonitoringapp.data.response.UserDataResponse
import junit.framework.TestCase.assertEquals
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


/**
 * Unit Test: UserRepository
 * Menguji logika getProfile, getLocalProfile, updateProfile, dan changePassword
 * menggunakan mock ApiService, SessionManager, dan UserProfileDao.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockApi: ApiService
    private lateinit var mockSession: SessionManager
    private lateinit var mockProfileDao: UserProfileDao
    private lateinit var repository: UserRepository

    private val sampleUser = UserData(
        id = 1,
        name = "Imam",
        email = "imam@unila.ac.id",
        nip = "12345",
        role = "admin",
        createdAt = "2025-01-01"
    )

    private val sampleEntity = UserProfileEntity(
        id = 1,
        name = "Imam Lokal",
        email = "imam@unila.ac.id",
        nip = "12345",
        role = "admin",
        createdAt = "2025-01-01"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockApi = mock()
        mockSession = mock()
        mockProfileDao = mock()
        repository = UserRepository(mockApi, mockSession, mockProfileDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── getProfile ─────────────────────────────────────────────────────────

    /**
     * TC-UREPO-01
     * Verifikasi bahwa getProfile sukses menyimpan data ke Room.
     */
    @Test
    fun `getProfile sukses menyimpan ke Room`() = runTest {
        val response = Response.success(UserDataResponse(success = true, data = sampleUser))
        whenever(mockApi.getProfile()).thenReturn(response)

        val result = repository.getProfile()

        assertTrue(result.isSuccess)
        verify(mockProfileDao).saveProfile(any())
    }

    /**
     * TC-UREPO-02
     * Verifikasi bahwa getProfile tidak crash saat API gagal.
     */
    @Test
    fun `getProfile gagal tidak crash`() = runTest {
        whenever(mockApi.getProfile()).thenThrow(RuntimeException("Network error"))

        val result = repository.getProfile()

        assertTrue(result.isFailure)
    }


    // ─── getLocalProfile ─────────────────────────────────────────────────────

    /**
     * TC-UREPO-04
     * Verifikasi bahwa getLocalProfile mengembalikan data dari DAO.
     */
    @Test
    fun `getLocalProfile mengembalikan data dari DAO`() = runTest {
        whenever(mockProfileDao.getProfile()).thenReturn(sampleEntity)

        val result = repository.getLocalProfile()

        assertNotNull(result)
        assertEquals("Imam Lokal", result?.name)
        assertEquals("imam@unila.ac.id", result?.email)
        assertEquals("12345", result?.nip)
        assertEquals("admin", result?.role)
    }

    /**
     * TC-UREPO-05
     * Verifikasi bahwa getLocalProfile mengembalikan null saat DAO kosong.
     */
    @Test
    fun `getLocalProfile null saat DAO kosong`() = runTest {
        whenever(mockProfileDao.getProfile()).thenReturn(null)

        val result = repository.getLocalProfile()

        assertNull(result)
    }

    // ─── updateProfile ───────────────────────────────────────────────────────

    /**
     * TC-UREPO-06
     * Verifikasi bahwa updateProfile sukses menyimpan ke Room dan session.
     */
    @Test
    fun `updateProfile sukses menyimpan ke Room dan session`() = runTest {
        val updatedUser = sampleUser.copy(name = "Imam Baru")
        val response = Response.success(
            UpdateProfileResponse(success = true, message = "ok", data = updatedUser)
        )
        whenever(mockApi.updateProfile(any())).thenReturn(response)

        val result = repository.updateProfile(name = "Imam Baru")

        assertTrue(result.isSuccess)
        verify(mockProfileDao).saveProfile(any())
        verify(mockSession).saveProfileData(
            name = "Imam Baru",
            email = "imam@unila.ac.id",
            nip = "12345"
        )
    }


    // ─── changePassword ──────────────────────────────────────────────────────

    /**
     * TC-UREPO-08
     * Verifikasi bahwa changePassword sukses menyimpan token baru.
     */
    @Test
    fun `changePassword sukses menyimpan token baru`() = runTest {
        val response = Response.success(
            ChangePasswordResponse(success = true, message = "ok", token = "new_token_123")
        )
        whenever(mockApi.changePassword(any())).thenReturn(response)

        val result = repository.changePassword("lama", "baru")

        assertTrue(result.isSuccess)
        verify(mockSession).saveToken("new_token_123")
    }

    /**
     * TC-UREPO-09
     * Verifikasi bahwa changePassword tanpa token baru tidak menyimpan.
     */
    @Test
    fun `changePassword tanpa token tidak menyimpan`() = runTest {
        val response = Response.success(
            ChangePasswordResponse(success = true, message = "ok", token = null)
        )
        whenever(mockApi.changePassword(any())).thenReturn(response)

        val result = repository.changePassword("lama", "baru")

        assertTrue(result.isSuccess)
        verify(mockSession, never()).saveToken(any())
    }

    /**
     * TC-UREPO-10
     * Verifikasi bahwa changePassword network failure menghasilkan Result.failure.
     */
    @Test
    fun `changePassword network failure`() = runTest {
        whenever(mockApi.changePassword(any())).thenThrow(RuntimeException("Timeout"))

        val result = repository.changePassword("lama", "baru")

        assertTrue(result.isFailure)
    }
}
