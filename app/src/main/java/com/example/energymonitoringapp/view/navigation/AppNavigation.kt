package com.example.energymonitoringapp.view.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.view.screen.admin.dashboard.dashboardAdminScreen
import com.example.energymonitoringapp.view.screen.admin.login.LoginAdminScreen
import com.example.energymonitoringapp.view.screen.admin.notifikasi.detailNotifAdminScreen
import com.example.energymonitoringapp.view.screen.admin.notifikasi.notifikasiAdminScreen
import com.example.energymonitoringapp.view.screen.admin.pemantauan.pemantauanScreen
import com.example.energymonitoringapp.view.screen.common.pengaturan.EditProfilFormScreen
import com.example.energymonitoringapp.view.screen.common.pengaturan.batasnilaiParameterScreen
import com.example.energymonitoringapp.view.screen.common.pengaturan.editProfilScreen
import com.example.energymonitoringapp.view.screen.common.pengaturan.pengaturanAdminScreen
import com.example.energymonitoringapp.view.screen.common.pengaturan.pengaturanScreen
import com.example.energymonitoringapp.view.screen.common.pengaturan.ubahPasswordScreen
import com.example.energymonitoringapp.view.screen.common.splash.splashScreen
import com.example.energymonitoringapp.view.screen.teknisi.dashboard.dashboardScreen
import com.example.energymonitoringapp.view.screen.teknisi.login.LoginTeknisiScreen
import com.example.energymonitoringapp.view.screen.teknisi.notifikasi.detailNotifScreen
import com.example.energymonitoringapp.view.screen.teknisi.notifikasi.konfirmasiPenangananScreen
import com.example.energymonitoringapp.view.screen.teknisi.notifikasi.notifikasiScreen

object Routes {
    // Common
    const val SPLASH = "splash"
    const val UBAH_PASSWORD = "ubah_password"
    const val EDIT_PROFIL = "edit_profil"
    const val EDIT_PROFIL_FORM = "edit_profil_form"
    const val BATAS_NILAI = "batas_nilai"
    const val PENGATURAN_TEKNISI = "pengaturan_teknisi"
    const val PENGATURAN_ADMIN = "pengaturan_admin"

    // Teknisi
    const val LOGIN_TEKNISI = "login_teknisi"
    const val DASHBOARD_TEKNISI = "dashboard_teknisi"
    const val NOTIFIKASI_TEKNISI = "notifikasi_teknisi"
    const val DETAIL_NOTIF_TEKNISI = "detail_notif_teknisi/{notifId}"
    const val KONFIRMASI_PENANGANAN = "konfirmasi_penanganan/{notifId}"

    // Admin
    const val LOGIN_ADMIN = "login_admin"
    const val DASHBOARD_ADMIN = "dashboard_admin"
    const val PEMANTAUAN_ADMIN = "pemantauan_admin"
    const val NOTIFIKASI_ADMIN = "notifikasi_admin"
    const val DETAIL_NOTIF_ADMIN = "detail_notif_admin/{notifId}"

    fun detailNotifTeknisi(notifId: Int) = "detail_notif_teknisi/$notifId"
    fun detailNotifAdmin(notifId: Int) = "detail_notif_admin/$notifId"
    fun konfirmasiPenanganan(notifId: Int) = "konfirmasi_penanganan/$notifId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context       = LocalContext.current
    val session       = remember { SessionManager(context) }

    val startDestination = when {
        session.isLoggedIn() && session.isAdmin()   -> Routes.DASHBOARD_ADMIN
        session.isLoggedIn() && session.isTeknisi() -> Routes.DASHBOARD_TEKNISI
        else                                        -> Routes.SPLASH
    }

    fun navigateTab(route: String) {
        val homeRoute = if (session.isAdmin()) Routes.DASHBOARD_ADMIN
        else Routes.DASHBOARD_TEKNISI
        navController.navigate(route) {
            popUpTo(homeRoute) {
                saveState = true
            }
            launchSingleTop = true
            restoreState    = true
        }
    }

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            splashScreen(
                onAdminClick   = { navController.navigate(Routes.LOGIN_ADMIN) },
                onTeknisiClick = { navController.navigate(Routes.LOGIN_TEKNISI) }
            )
        }

        composable(Routes.LOGIN_TEKNISI) {
            LoginTeknisiScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD_TEKNISI) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.LOGIN_ADMIN) {
            LoginAdminScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD_ADMIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.UBAH_PASSWORD) {
            ubahPasswordScreen(
                onBackClick   = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(Routes.EDIT_PROFIL) {
            editProfilScreen(
                onBackClick  = { navController.popBackStack() },
                onEditClick  = { navController.navigate(Routes.EDIT_PROFIL_FORM) }
            )
        }

        composable(Routes.EDIT_PROFIL_FORM) {
            EditProfilFormScreen(
                onBackClick   = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(Routes.BATAS_NILAI) {
            batasnilaiParameterScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD_TEKNISI) {
            val activity = LocalActivity.current
            BackHandler { activity?.finish() }
            dashboardScreen(
                onNavigateTo = { route -> navigateTab(route) }
            )
        }

        composable(Routes.NOTIFIKASI_TEKNISI) {
            notifikasiScreen(
                onNavigateTo = { route -> navigateTab(route) },
                onNotifClick = { notifId ->
                    navController.navigate(Routes.detailNotifTeknisi(notifId))
                }
            )
        }

        composable(Routes.DETAIL_NOTIF_TEKNISI) { backStackEntry ->
            val notifId = backStackEntry.arguments?.getString("notifId")?.toIntOrNull() ?: 0
            detailNotifScreen(
                notifId           = notifId,
                onBackClick       = { navController.popBackStack() },
                onKonfirmasiClick = { id ->
                    navController.navigate(Routes.konfirmasiPenanganan(id))
                }
            )
        }

        composable(Routes.KONFIRMASI_PENANGANAN) { backStackEntry ->
            val notifId = backStackEntry.arguments?.getString("notifId")?.toIntOrNull() ?: 0
            konfirmasiPenangananScreen(
                notifId        = notifId,
                onBackClick    = { navController.popBackStack() },
                onKirimSuccess = {
                    navController.navigate(Routes.NOTIFIKASI_TEKNISI) {
                        popUpTo(Routes.NOTIFIKASI_TEKNISI) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.PENGATURAN_TEKNISI) {
            pengaturanScreen(
                onNavigateTo        = { route -> navigateTab(route) },
                onUbahPasswordClick = { navController.navigate(Routes.UBAH_PASSWORD) },
                onEditProfilClick   = { navController.navigate(Routes.EDIT_PROFIL) },
                onBatasNilaiClick   = { navController.navigate(Routes.BATAS_NILAI) },
                onKeluarConfirmed   = {
                    session.clearSession()
                    navController.navigate(Routes.SPLASH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD_ADMIN) {
            val activity = LocalActivity.current
            BackHandler { activity?.finish() }
            dashboardAdminScreen(
                onNavigateTo = { route -> navigateTab(route) }
            )
        }
        composable(Routes.PEMANTAUAN_ADMIN) {
            pemantauanScreen(
                onNavigateTo = { route -> navigateTab(route) }
            )
        }

        composable(Routes.NOTIFIKASI_ADMIN) {
            notifikasiAdminScreen(
                onNavigateTo = { route -> navigateTab(route) },
                onNotifClick = { notifId ->
                    navController.navigate(Routes.detailNotifAdmin(notifId))
                }
            )
        }

        composable(Routes.DETAIL_NOTIF_ADMIN) { backStackEntry ->
            val notifId = backStackEntry.arguments?.getString("notifId")?.toIntOrNull() ?: 0
            detailNotifAdminScreen(
                notifId        = notifId,
                onBackClick    = { navController.popBackStack() },
                onKirimSuccess = {
                    navController.navigate(Routes.NOTIFIKASI_ADMIN) {
                        popUpTo(Routes.NOTIFIKASI_ADMIN) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.PENGATURAN_ADMIN) {
            pengaturanAdminScreen(
                onNavigateTo        = { route -> navigateTab(route) },
                onUbahPasswordClick = { navController.navigate(Routes.UBAH_PASSWORD) },
                onEditProfilClick   = { navController.navigate(Routes.EDIT_PROFIL) },
                onBatasNilaiClick   = { navController.navigate(Routes.BATAS_NILAI) },
                onKeluarConfirmed   = {
                    session.clearSession()
                    navController.navigate(Routes.SPLASH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}