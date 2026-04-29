<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\MonitoringLingkunganController;
use App\Http\Controllers\Api\ListrikController;
use App\Http\Controllers\Api\DayaController;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\AdminUserController;
use App\Http\Controllers\Api\Admin\NotificationController as AdminNotificationController;
use App\Http\Controllers\Api\Technician\NotificationController as TechnicianNotificationController;
use App\Models\Notification;

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

Route::middleware('auth:sanctum')->prefix('admin')->group(function () {
    Route::get('/users', [AdminUserController::class, 'index'])->name('admin.users.index');
    Route::post('/users', [AdminUserController::class, 'store'])->name('admin.users.store');
});

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

Route::middleware('auth:sanctum')->prefix('admin')->group(function () {
    Route::get('/users', [AdminUserController::class, 'index']);
    Route::post('/users', [AdminUserController::class, 'store']);

    Route::get('/teknisi', [AdminUserController::class, 'teknisi']);
});
Route::middleware(['auth:sanctum'])->prefix('technician')->group(function () {
    Route::get('/notifications', [TechnicianNotificationController::class, 'index']);
    Route::get('/notifications/{id}', [TechnicianNotificationController::class, 'show']);
    Route::post('/notifications/{id}/complete', [TechnicianNotificationController::class, 'complete']);
});
/*
|--------------------------------------------------------------------------
| MONITORING LINGKUNGAN
|--------------------------------------------------------------------------
*/
Route::prefix('monitoring')->group(function () {

    Route::post('/store', [MonitoringLingkunganController::class, 'store']);

    Route::get('/suhu', [MonitoringLingkunganController::class, 'getSuhu']);
    Route::get('/kelembapan', [MonitoringLingkunganController::class, 'getKelembapan']);
    Route::get('/pm25', [MonitoringLingkunganController::class, 'getPm25']);
    Route::get('/pm10', [MonitoringLingkunganController::class, 'getPm10']);
    Route::get('/gas-co', [MonitoringLingkunganController::class, 'getGasCo']);
    Route::get('/gas-co2', [MonitoringLingkunganController::class, 'getGasCo2']);
    Route::get('/cahaya', [MonitoringLingkunganController::class, 'getCahaya']);
    Route::get('/kebisingan', [MonitoringLingkunganController::class, 'getKebisingan']);
    Route::get('/lokasi', [MonitoringLingkunganController::class, 'getLokasi']);

    Route::get('/ieq', [MonitoringLingkunganController::class, 'getIEQ']);

    Route::get('/all', [MonitoringLingkunganController::class, 'getAll']);
    Route::get('/history', [MonitoringLingkunganController::class, 'getHistory']);
});


/*
|--------------------------------------------------------------------------
| LISTRIK
|--------------------------------------------------------------------------
*/
Route::prefix('listrik')->group(function () {

    Route::post('/store', [ListrikController::class, 'store']);

    Route::get('/voltage-l1l2', [ListrikController::class, 'getVoltageL1L2']);
    Route::get('/voltage-l2l3', [ListrikController::class, 'getVoltageL2L3']);
    Route::get('/voltage-l3l1', [ListrikController::class, 'getVoltageL3L1']);

    Route::get('/voltage-l1n', [ListrikController::class, 'getVoltageL1N']);
    Route::get('/voltage-l2n', [ListrikController::class, 'getVoltageL2N']);
    Route::get('/voltage-l3n', [ListrikController::class, 'getVoltageL3N']);

    Route::get('/current-l1', [ListrikController::class, 'getCurrentL1']);
    Route::get('/current-l2', [ListrikController::class, 'getCurrentL2']);
    Route::get('/current-l3', [ListrikController::class, 'getCurrentL3']);
    Route::get('/current-n', [ListrikController::class, 'getCurrentN']);

    Route::get('/total-voltage', [ListrikController::class, 'getTotalVoltage']);
    Route::get('/total-current', [ListrikController::class, 'getTotalCurrent']);

    Route::get('/frecuency', [ListrikController::class, 'getFrecuency']);
    Route::get('/power-factor', [ListrikController::class, 'getPowerFactor']);

    Route::get('/all', [ListrikController::class, 'getAll']);
    Route::get('/history', [ListrikController::class, 'getHistory']);
});


/*
|--------------------------------------------------------------------------
| DAYA
|--------------------------------------------------------------------------
*/
Route::prefix('daya')->group(function () {

    Route::post('/store', [DayaController::class, 'store']);

    Route::get('/estimasi-harian', [DayaController::class, 'getEstimasiHarian']);
    Route::get('/estimasi-bulanan', [DayaController::class, 'getEstimasiBulanan']);
    Route::get('/estimasi-tahunan', [DayaController::class, 'getEstimasiTahunan']);

    Route::get('/active-power-r', [DayaController::class, 'getActivePowerR']);
    Route::get('/active-power-s', [DayaController::class, 'getActivePowerS']);
    Route::get('/active-power-t', [DayaController::class, 'getActivePowerT']);
    Route::get('/total-active-power', [DayaController::class, 'getTotalActivePower'])->name('daya.total_active_power');
    Route::get('/reactive-power-r', [DayaController::class, 'getReactivePowerR']);
    Route::get('/reactive-power-s', [DayaController::class, 'getReactivePowerS']);
    Route::get('/reactive-power-t', [DayaController::class, 'getReactivePowerT']);

    Route::get('/apparent-power-r', [DayaController::class, 'getApparentPowerR']);
    Route::get('/apparent-power-s', [DayaController::class, 'getApparentPowerS']);
    Route::get('/apparent-power-t', [DayaController::class, 'getApparentPowerT']);

    Route::get('/all', [DayaController::class, 'getAll']);
    Route::get('/history', [DayaController::class, 'getHistory']);
});


/*
|--------------------------------------------------------------------------
| NOTIFICATIONS (🔥 INI YANG KAMU BUTUH)
|--------------------------------------------------------------------------
*/
Route::middleware(['auth:sanctum'])->prefix('technician')->group(function () {
    Route::get('/notifications', [TechnicianNotificationController::class, 'index']);
    Route::get('/notifications/{id}', [TechnicianNotificationController::class, 'show']);
    Route::post('/notifications/{id}/complete', [TechnicianNotificationController::class, 'complete']);
});

Route::prefix('notifications')->group(function () {

    // Ambil semua notif
    Route::get('/', function () {
        return Notification::orderBy('created_at', 'desc')->get();
    });

});

Route::middleware(['auth:sanctum'])->prefix('admin')->group(function () {
    Route::get('/notifications', [AdminNotificationController::class, 'index']);
    Route::get('/notifications/{id}', [AdminNotificationController::class, 'show']);
    Route::post('/notifications/{id}/assign', [AdminNotificationController::class, 'assign']);
});

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
                'suhu','kelembapan','pm25','pm10','gas_co','gas_co2','cahaya','kebisingan','lokasi'
            ],
            'listrik' => [
                'voltage_l1l2','voltage_l2l3','voltage_l3l1',
                'voltage_l1n','voltage_l2n','voltage_l3n',
                'current_l1','current_l2','current_l3','current_n',
                'frecuency','power_factor'
            ],
            'daya' => [
                'active_power_r','active_power_s','active_power_t',
                'reactive_power_r','reactive_power_s','reactive_power_t',
                'apparent_power_r','apparent_power_s','apparent_power_t'
            ],
            'notification' => [
                'notification.updated'
            ]
        ],
    ]);
});