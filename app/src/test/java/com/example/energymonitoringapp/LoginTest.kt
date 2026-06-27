package com.example.energymonitoringapp

import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.repository.AuthRepository
import com.example.energymonitoringapp.data.response.LoginData
import com.example.energymonitoringapp.data.response.LoginResponse
import com.example.energymonitoringapp.viewmodel.AuthUiState
import com.example.energymonitoringapp.viewmodel.AuthViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockRepository: AuthRepository
    private lateinit var mockSession: SessionManager
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        mockSession    = mock()
        viewModel      = AuthViewModel(mockRepository, mockSession)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── State Awal ──────────────────────────────────────────────────────────

    /**
     * TC-VM-01
     * Verifikasi bahwa state awal AuthViewModel adalah Idle.
     */
    @Test
    fun `state awal adalah Idle`() = runTest {
        val state = viewModel.uiState.first()
        assertTrue("State awal seharusnya Idle", state is AuthUiState.Idle)
    }

    // ─── Login Sukses ─────────────────────────────────────────────────────────

    /**
     * TC-VM-02
     * Verifikasi bahwa login dengan kredensial valid dan peran "admin"
     * menghasilkan state Success dengan role admin.
     */
    @Test
    fun `login sukses sebagai admin menghasilkan state Success`() = runTest {
        val responseSukses = LoginResponse(
            success = true,
            message = "Login berhasil",
            data    = LoginData(
                token = "token_abc",
                role  = SessionManager.ROLE_ADMIN,
                name  = "Admin"
            )
        )
        whenever(mockRepository.login("admin@example.com", "admin12345"))
            .thenReturn(Result.success(responseSukses))

        viewModel.login(
            email        = "admin@example.com",
            password     = "admin12345",
            expectedRole = SessionManager.ROLE_ADMIN
        )

        val state = viewModel.uiState.value
        assertTrue("State seharusnya Success", state is AuthUiState.Success)
        assertEquals(
            "Role seharusnya admin",
            SessionManager.ROLE_ADMIN,
            (state as AuthUiState.Success).role
        )
        assertEquals("Nama seharusnya Admin", "Admin", state.name)
    }

    /**
     * TC-VM-03
     * Verifikasi bahwa login dengan kredensial valid dan peran "teknisi"
     * menghasilkan state Success dengan role teknisi.
     */
    @Test
    fun `login sukses sebagai teknisi menghasilkan state Success`() = runTest {
        val responseSukses = LoginResponse(
            success = true,
            message = "Login berhasil",
            data    = LoginData(
                token = "token_xyz",
                role  = SessionManager.ROLE_TEKNISI,
                name  = "Teknisi"
            )
        )
        whenever(mockRepository.login("teknisi@example.com", "teknisi1234"))
            .thenReturn(Result.success(responseSukses))

        viewModel.login(
            email        = "teknisi@example.com",
            password     = "teknisi1234",
            expectedRole = SessionManager.ROLE_TEKNISI
        )

        val state = viewModel.uiState.value
        assertTrue("State seharusnya Success", state is AuthUiState.Success)
        assertEquals(SessionManager.ROLE_TEKNISI, (state as AuthUiState.Success).role)
    }

    // ─── Login Gagal: Peran Salah ─────────────────────────────────────────────

    /**
     * TC-VM-04
     * Verifikasi bahwa login dengan akun admin namun expectedRole teknisi
     * menghasilkan state Error dengan pesan yang sesuai.
     */
    @Test
    fun `login akun admin dengan expectedRole teknisi menghasilkan Error`() = runTest {
        val responseSukses = LoginResponse(
            success = true,
            message = "Login berhasil",
            data    = LoginData(
                token = "token_abc",
                role  = SessionManager.ROLE_ADMIN,
                name  = "Admin User"
            )
        )
        whenever(mockRepository.login(any(), any()))
            .thenReturn(Result.success(responseSukses))

        viewModel.login(
            email        = "admin@example.com",
            password     = "admin12345",
            expectedRole = SessionManager.ROLE_TEKNISI // salah role
        )

        val state = viewModel.uiState.value
        assertTrue("State seharusnya Error karena peran salah", state is AuthUiState.Error)
        val pesan = (state as AuthUiState.Error).message
        assertTrue("Pesan error harus menyebut 'Teknisi'", pesan.contains("Teknisi"))
    }

    // ─── Login Gagal: Jaringan Error ──────────────────────────────────────────

    /**
     * TC-VM-05
     * Verifikasi bahwa kegagalan jaringan saat login menghasilkan state Error.
     */
    @Test
    fun `kegagalan jaringan menghasilkan state Error`() = runTest {
        whenever(mockRepository.login(any(), any()))
            .thenReturn(Result.failure(Exception("Koneksi timeout")))

        viewModel.login(
            email        = "admin@example.com",
            password     = "admin12345",
            expectedRole = SessionManager.ROLE_ADMIN
        )

        val state = viewModel.uiState.value
        assertTrue("State seharusnya Error", state is AuthUiState.Error)
        val pesan = (state as AuthUiState.Error).message
        assertTrue("Pesan error harus ada", pesan.isNotEmpty())
    }

    // ─── Reset State ─────────────────────────────────────────────────────────

    /**
     * TC-VM-06
     * Verifikasi bahwa pemanggilan resetState() mengembalikan state ke Idle.
     */
    @Test
    fun `resetState mengembalikan state ke Idle`() = runTest {
        // Set state ke Error terlebih dahulu
        whenever(mockRepository.login(any(), any()))
            .thenReturn(Result.failure(Exception("Error")))
        viewModel.login("x@x.com", "pass", SessionManager.ROLE_ADMIN)

        // Pastikan state bukan Idle
        assertTrue(viewModel.uiState.value !is AuthUiState.Idle)

        // Reset
        viewModel.resetState()

        assertTrue("Setelah reset, state seharusnya Idle", viewModel.uiState.value is AuthUiState.Idle)
    }
}
