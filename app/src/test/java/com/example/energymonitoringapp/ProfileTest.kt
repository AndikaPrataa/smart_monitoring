package com.example.energymonitoringapp

import com.example.energymonitoringapp.data.repository.UserRepository
import com.example.energymonitoringapp.data.response.ChangePasswordResponse
import com.example.energymonitoringapp.data.response.UpdateProfileResponse
import com.example.energymonitoringapp.data.response.UserData
import com.example.energymonitoringapp.data.response.UserDataResponse
import com.example.energymonitoringapp.viewmodel.ProfileViewModel
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
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import retrofit2.Response


/**
 * Unit Test: ProfileViewModel
 * Menguji logika loadProfile, updateProfile, changePassword, dan clearMessages
 * menggunakan mock UserRepository.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockRepository: UserRepository
    private lateinit var viewModel: ProfileViewModel

    // Helper: UserData sample
    private val sampleUser = UserData(
        id = 1,
        name = "Imam",
        email = "imam@unila.ac.id",
        nip = "12345",
        role = "admin",
        createdAt = "2025-01-01"
    )

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
     * Helper: buat ViewModel dengan default init mock (loadProfile gagal).
     * Digunakan untuk test yang tidak peduli dengan loadProfile di init.
     */
    private suspend fun createViewModelWithFailedInit(): ProfileViewModel {
        whenever(mockRepository.getProfile())
            .thenReturn(Result.failure(Exception("init")))
        whenever(mockRepository.getLocalProfile()).thenReturn(null)
        return ProfileViewModel(mockRepository)
    }

    // ─── loadProfile ──────────────────────────────────────────────────────────

    /**
     * TC-PROFILE-01
     * Verifikasi bahwa loadProfile sukses menampilkan user data.
     */
    @Test
    fun `loadProfile sukses menampilkan user data`() = runTest {
        val successResponse = Response.success(UserDataResponse(success = true, data = sampleUser))
        whenever(mockRepository.getProfile()).thenReturn(Result.success(successResponse))

        viewModel = ProfileViewModel(mockRepository)

        assertEquals(sampleUser, viewModel.user.value)
    }

    /**
     * TC-PROFILE-02
     * Verifikasi bahwa loadProfile fallback ke data lokal saat API gagal.
     */
    @Test
    fun `loadProfile API gagal fallback ke lokal`() = runTest {
        whenever(mockRepository.getProfile())
            .thenReturn(Result.failure(Exception("Network error")))
        whenever(mockRepository.getLocalProfile()).thenReturn(sampleUser)

        viewModel = ProfileViewModel(mockRepository)

        assertEquals("Imam", viewModel.user.value?.name)
    }

    // ─── updateProfile ────────────────────────────────────────────────────────

    /**
     * TC-PROFILE-04
     * Verifikasi bahwa updateProfile dengan semua field kosong menghasilkan error.
     */
    @Test
    fun `updateProfile semua field kosong menghasilkan error`() = runTest {
        viewModel = createViewModelWithFailedInit()

        viewModel.updateProfile(name = "", email = "", nip = "")

        assertEquals("Tidak ada perubahan yang disimpan", viewModel.errorMessage.value)
    }

    /**
     * TC-PROFILE-05
     * Verifikasi bahwa updateProfile dengan semua field null menghasilkan error.
     */
    @Test
    fun `updateProfile semua field null menghasilkan error`() = runTest {
        viewModel = createViewModelWithFailedInit()

        viewModel.updateProfile(name = null, email = null, nip = null)

        assertEquals("Tidak ada perubahan yang disimpan", viewModel.errorMessage.value)
    }

    /**
     * TC-PROFILE-06
     * Verifikasi bahwa updateProfile sukses mengupdate user dan menampilkan success message.
     */
    @Test
    fun `updateProfile sukses mengupdate user dan success message`() = runTest {
        viewModel = createViewModelWithFailedInit()

        val updatedUser = sampleUser.copy(name = "Imam Baru")
        val successResponse = Response.success(
            UpdateProfileResponse(
                success = true,
                message = "Profil berhasil diperbarui",
                data = updatedUser
            )
        )
        doReturn(Result.success(successResponse))
            .whenever(mockRepository).updateProfile(anyOrNull(), anyOrNull(), anyOrNull())

        viewModel.updateProfile(name = "Imam Baru")

        assertEquals("Imam Baru", viewModel.user.value?.name)
        assertNotNull(viewModel.updateSuccess.value)
    }


    /**
     * TC-PROFILE-08
     * Verifikasi bahwa updateProfile network failure menghasilkan error.
     */
    @Test
    fun `updateProfile network failure menghasilkan error`() = runTest {
        viewModel = createViewModelWithFailedInit()

        doReturn(Result.failure<Response<UpdateProfileResponse>>(Exception("Koneksi timeout")))
            .whenever(mockRepository).updateProfile(anyOrNull(), anyOrNull(), anyOrNull())

        viewModel.updateProfile(name = "Test")

        assertEquals("Koneksi timeout", viewModel.errorMessage.value)
    }

    // ─── changePassword ───────────────────────────────────────────────────────

    /**
     * TC-PROFILE-09
     * Verifikasi bahwa password saat ini kosong menghasilkan error.
     */
    @Test
    fun `changePassword current kosong menghasilkan error`() = runTest {
        viewModel = createViewModelWithFailedInit()

        viewModel.changePassword(current = "", new = "baru123", confirm = "baru123")

        assertEquals("Password saat ini tidak boleh kosong", viewModel.errorMessage.value)
    }

    /**
     * TC-PROFILE-10
     * Verifikasi bahwa password baru kurang dari 6 karakter menghasilkan error.
     */
    @Test
    fun `changePassword baru kurang 6 karakter error`() = runTest {
        viewModel = createViewModelWithFailedInit()

        viewModel.changePassword(current = "lama123", new = "abc", confirm = "abc")

        assertEquals("Password minimal 6 karakter", viewModel.errorMessage.value)
    }

    /**
     * TC-PROFILE-11
     * Verifikasi bahwa konfirmasi password tidak cocok menghasilkan error.
     */
    @Test
    fun `changePassword konfirmasi tidak cocok error`() = runTest {
        viewModel = createViewModelWithFailedInit()

        viewModel.changePassword(current = "lama123", new = "baru123", confirm = "beda456")

        assertEquals("Konfirmasi password tidak cocok", viewModel.errorMessage.value)
    }

    /**
     * TC-PROFILE-12
     * Verifikasi bahwa changePassword sukses menampilkan success message.
     */
    @Test
    fun `changePassword sukses menampilkan success message`() = runTest {
        viewModel = createViewModelWithFailedInit()

        val successResponse = Response.success(
            ChangePasswordResponse(
                success = true,
                message = "Password berhasil diubah",
                token = "new_token"
            )
        )
        whenever(mockRepository.changePassword(any(), any()))
            .thenReturn(Result.success(successResponse))

        viewModel.changePassword(current = "lama123", new = "baru1234", confirm = "baru1234")

        assertNotNull(viewModel.updateSuccess.value)
        assertTrue(viewModel.updateSuccess.value!!.contains("Password"))
    }

    /**
     * TC-PROFILE-13
     * Verifikasi bahwa changePassword gagal menampilkan error dari server.
     */
    @Test
    fun `changePassword gagal menampilkan error dari server`() = runTest {
        viewModel = createViewModelWithFailedInit()

        val failResponse = Response.success(
            ChangePasswordResponse(
                success = false,
                message = "Password lama salah",
                token = null
            )
        )
        whenever(mockRepository.changePassword(any(), any()))
            .thenReturn(Result.success(failResponse))

        viewModel.changePassword(current = "salah", new = "baru1234", confirm = "baru1234")

        assertEquals("Password lama salah", viewModel.errorMessage.value)
    }

    // ─── clearMessages ────────────────────────────────────────────────────────

    /**
     * TC-PROFILE-14
     * Verifikasi bahwa clearMessages mengosongkan error dan success.
     */
    @Test
    fun `clearMessages mengosongkan error dan success`() = runTest {
        viewModel = createViewModelWithFailedInit()

        // Trigger error
        viewModel.updateProfile(name = "", email = "", nip = "")
        assertNotNull(viewModel.errorMessage.value)

        // Clear
        viewModel.clearMessages()

        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.updateSuccess.value)
    }
}
