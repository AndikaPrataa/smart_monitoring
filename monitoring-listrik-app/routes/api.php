<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Models\Notification;

use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\AdminUserController;
use App\Http\Controllers\Api\ListrikController;
use App\Http\Controllers\Api\DayaController;
use App\Http\Controllers\Api\MonitoringLingkunganController;
use App\Http\Controllers\Api\ChartController;

use App\Http\Controllers\Api\Admin\NotificationController as AdminNotificationController;
use App\Http\Controllers\Api\Technician\NotificationController as TechnicianNotificationController;

/*
|--------------------------------------------------------------------------
| AUTH
|--------------------------------------------------------------------------
*/
Route::prefix('auth')->group(function () {
    Route::post('/register', [AuthController::class, 'register'])->name('auth.register');
    Route::post('/login', [AuthController::class, 'login'])->name('auth.login');

    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/logout', [AuthController::class, 'logout'])->name('auth.logout');
        Route::get('/me', [AuthController::class, 'me'])->name('auth.me');
    });
});

/*
|--------------------------------------------------------------------------
| USER LOGIN
|--------------------------------------------------------------------------
*/
Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

/*
|--------------------------------------------------------------------------
| ADMIN
|--------------------------------------------------------------------------
*/
Route::middleware('auth:sanctum')->prefix('admin')->group(function () {
    Route::get('/users', [AdminUserController::class, 'index'])->name('admin.users.index');
    Route::post('/users', [AdminUserController::class, 'store'])->name('admin.users.store');
    Route::put('/users/{id}', [AdminUserController::class, 'update'])->name('admin.users.update');
    Route::delete('/users/{id}', [AdminUserController::class, 'destroy'])->name('admin.users.destroy');
    Route::get('/teknisi', [AdminUserController::class, 'teknisi'])->name('admin.users.teknisi');

    Route::get('/notifications', [AdminNotificationController::class, 'index'])->name('admin.notifications.index');
    Route::get('/notifications/{id}', [AdminNotificationController::class, 'show'])->name('admin.notifications.show');
    Route::post('/notifications/{id}/assign', [AdminNotificationController::class, 'assign'])->name('admin.notifications.assign');
});

/*
|--------------------------------------------------------------------------
| TECHNICIAN
|--------------------------------------------------------------------------
*/
Route::middleware('auth:sanctum')->prefix('technician')->group(function () {
    Route::get('/notifications', [TechnicianNotificationController::class, 'index'])->name('technician.notifications.index');
    Route::get('/notifications/{id}', [TechnicianNotificationController::class, 'show'])->name('technician.notifications.show');
    Route::post('/notifications/{id}/complete', [TechnicianNotificationController::class, 'complete'])->name('technician.notifications.complete');
});

/*
|--------------------------------------------------------------------------
| MONITORING LINGKUNGAN
|--------------------------------------------------------------------------
*/
Route::prefix('monitoring')->group(function () {
    Route::post('/store', [MonitoringLingkunganController::class, 'store'])->name('monitoring.store');

    Route::get('/suhu', [MonitoringLingkunganController::class, 'getSuhu'])->name('monitoring.suhu');
    Route::get('/kelembapan', [MonitoringLingkunganController::class, 'getKelembapan'])->name('monitoring.kelembapan');
    Route::get('/pm25', [MonitoringLingkunganController::class, 'getPm25'])->name('monitoring.pm25');
    Route::get('/pm10', [MonitoringLingkunganController::class, 'getPm10'])->name('monitoring.pm10');
    Route::get('/gas-co', [MonitoringLingkunganController::class, 'getGasCo'])->name('monitoring.gas_co');
    Route::get('/gas-co2', [MonitoringLingkunganController::class, 'getGasCo2'])->name('monitoring.gas_co2');
    Route::get('/tvoc', [MonitoringLingkunganController::class, 'getTvoc'])->name('monitoring.tvoc');
    Route::get('/cahaya', [MonitoringLingkunganController::class, 'getCahaya'])->name('monitoring.cahaya');
    Route::get('/kebisingan', [MonitoringLingkunganController::class, 'getKebisingan'])->name('monitoring.kebisingan');
    Route::get('/lokasi', [MonitoringLingkunganController::class, 'getLokasi'])->name('monitoring.lokasi');

    Route::get('/ieq', [MonitoringLingkunganController::class, 'getIEQ'])->name('monitoring.ieq');
    Route::get('/all', [MonitoringLingkunganController::class, 'getAll'])->name('monitoring.all');
    Route::get('/history', [MonitoringLingkunganController::class, 'getHistory'])->name('monitoring.history');
});

/*
|--------------------------------------------------------------------------
| LISTRIK
|--------------------------------------------------------------------------
*/
Route::prefix('listrik')->group(function () {
    Route::post('/store', [ListrikController::class, 'store'])->name('listrik.store');

    Route::get('/voltage-l1l2', [ListrikController::class, 'getVoltageL1L2'])->name('listrik.voltage_l1l2');
    Route::get('/voltage-l2l3', [ListrikController::class, 'getVoltageL2L3'])->name('listrik.voltage_l2l3');
    Route::get('/voltage-l3l1', [ListrikController::class, 'getVoltageL3L1'])->name('listrik.voltage_l3l1');

    Route::get('/voltage-l1n', [ListrikController::class, 'getVoltageL1N'])->name('listrik.voltage_l1n');
    Route::get('/voltage-l2n', [ListrikController::class, 'getVoltageL2N'])->name('listrik.voltage_l2n');
    Route::get('/voltage-l3n', [ListrikController::class, 'getVoltageL3N'])->name('listrik.voltage_l3n');

    Route::get('/current-l1', [ListrikController::class, 'getCurrentL1'])->name('listrik.current_l1');
    Route::get('/current-l2', [ListrikController::class, 'getCurrentL2'])->name('listrik.current_l2');
    Route::get('/current-l3', [ListrikController::class, 'getCurrentL3'])->name('listrik.current_l3');
    Route::get('/current-n', [ListrikController::class, 'getCurrentN'])->name('listrik.current_n');

    Route::get('/total-voltage', [ListrikController::class, 'getTotalVoltage'])->name('listrik.total_voltage');
    Route::get('/total-current', [ListrikController::class, 'getTotalCurrent'])->name('listrik.total_current');

    Route::get('/frecuency', [ListrikController::class, 'getFrecuency'])->name('listrik.frecuency');
    Route::get('/power-factor', [ListrikController::class, 'getPowerFactor'])->name('listrik.power_factor');

    Route::get('/all', [ListrikController::class, 'getAll'])->name('listrik.all');
    Route::get('/history', [ListrikController::class, 'getHistory'])->name('listrik.history');
});

/*
|--------------------------------------------------------------------------
| DAYA
|--------------------------------------------------------------------------
*/
Route::prefix('daya')->group(function () {
    Route::post('/store', [DayaController::class, 'store'])->name('daya.store');

    Route::get('/active-power-r', [DayaController::class, 'getActivePowerR'])->name('daya.active_power_r');
    Route::get('/active-power-s', [DayaController::class, 'getActivePowerS'])->name('daya.active_power_s');
    Route::get('/active-power-t', [DayaController::class, 'getActivePowerT'])->name('daya.active_power_t');

    Route::get('/reactive-power-r', [DayaController::class, 'getReactivePowerR'])->name('daya.reactive_power_r');
    Route::get('/reactive-power-s', [DayaController::class, 'getReactivePowerS'])->name('daya.reactive_power_s');
    Route::get('/reactive-power-t', [DayaController::class, 'getReactivePowerT'])->name('daya.reactive_power_t');

    Route::get('/apparent-power-r', [DayaController::class, 'getApparentPowerR'])->name('daya.apparent_power_r');
    Route::get('/apparent-power-s', [DayaController::class, 'getApparentPowerS'])->name('daya.apparent_power_s');
    Route::get('/apparent-power-t', [DayaController::class, 'getApparentPowerT'])->name('daya.apparent_power_t');

    Route::get('/total-active-power', [DayaController::class, 'getTotalActivePower'])->name('daya.total_active_power');
    Route::get('/total-energi', [DayaController::class, 'getTotalEnergi'])->name('daya.total_energi');
    Route::get('/estimasi', [DayaController::class, 'getEstimasi'])->name('daya.estimasi');

    Route::get('/all', [DayaController::class, 'getAll'])->name('daya.all');
    Route::get('/history', [DayaController::class, 'getHistory'])->name('daya.history');
});

/*
|--------------------------------------------------------------------------
| CHART
|--------------------------------------------------------------------------
*/
Route::prefix('chart')->group(function () {
    Route::get('/monitoring', [ChartController::class, 'monitoring'])->name('chart.monitoring');
    Route::get('/listrik', [ChartController::class, 'listrik'])->name('chart.listrik');
    Route::get('/daya', [ChartController::class, 'daya'])->name('chart.daya');
});

/*
|--------------------------------------------------------------------------
| NOTIFICATIONS PUBLIC / DEBUG
|--------------------------------------------------------------------------
| Catatan:
| Route ini hanya untuk cek cepat semua notifikasi.
| Untuk sistem final, sebaiknya endpoint notifikasi tetap lewat admin/technician.
|--------------------------------------------------------------------------
*/
Route::get('/notifications', function () {
    return Notification::orderBy('created_at', 'desc')->get();
})->name('notifications.public');

/*
|--------------------------------------------------------------------------
| WEBSOCKET CHANNEL INFO
|--------------------------------------------------------------------------
*/
Route::get('/channels', function () {
    return response()->json([
        'status' => 'Broadcasting channels ready',
        'channels' => [
            'monitoring' => [
                'monitoring.suhu',
                'monitoring.kelembapan',
                'monitoring.pm25',
                'monitoring.pm10',
                'monitoring.gas_co',
                'monitoring.gas_co2',
                'monitoring.tvoc',
                'monitoring.cahaya',
                'monitoring.kebisingan',
                'monitoring.lokasi',
                'monitoring.ieq',
            ],
            'listrik' => [
                'listrik.voltage.l1l2',
                'listrik.voltage.l2l3',
                'listrik.voltage.l3l1',
                'listrik.voltage.l1n',
                'listrik.voltage.l2n',
                'listrik.voltage.l3n',
                'listrik.current.l1',
                'listrik.current.l2',
                'listrik.current.l3',
                'listrik.current.n',
                'listrik.total.voltage',
                'listrik.total.current',
                'listrik.frecuency',
                'listrik.power_factor',
            ],
            'daya' => [
                'daya.active.r',
                'daya.active.s',
                'daya.active.t',
                'daya.reactive.r',
                'daya.reactive.s',
                'daya.reactive.t',
                'daya.apparent.r',
                'daya.apparent.s',
                'daya.apparent.t',
                'daya',
                'daya.biaya',
            ],
            'notification' => [
                'notification.admin',
                'notification.technician.{id}',
            ],
            'chart_api' => [
                'api/chart/monitoring',
                'api/chart/listrik',
                'api/chart/daya',
            ],
        ],
    ]);
})->name('channels.info');