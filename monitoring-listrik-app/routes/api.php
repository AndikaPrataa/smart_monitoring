<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\MonitoringLingkunganController;
use App\Http\Controllers\Api\ListrikController;
use App\Http\Controllers\Api\DayaController;
use App\Http\Controllers\Api\AuthController;

/**
 * ============================================
 * AUTHENTICATION ENDPOINTS
 * ============================================
 */
Route::prefix('auth')->group(function () {
    // Public endpoints (tidak perlu token)
    Route::post('/register', [AuthController::class, 'register'])->name('auth.register');
    Route::post('/login', [AuthController::class, 'login'])->name('auth.login');

    // Protected endpoints (perlu token)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/logout', [AuthController::class, 'logout'])->name('auth.logout');
        Route::get('/me', [AuthController::class, 'me'])->name('auth.me');
    });
});

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

/**
 * ============================================
 * MONITORING LINGKUNGAN (ENVIRONMENT) ENDPOINTS
 * ============================================
 * 7 Individual sensor endpoints + all + history
 */
Route::prefix('monitoring')->group(function () {
    Route::get('/suhu', [MonitoringLingkunganController::class, 'getSuhu'])->name('monitoring.suhu');
    Route::get('/kelembapan', [MonitoringLingkunganController::class, 'getKelembapan'])->name('monitoring.kelembapan');
    Route::get('/pm25', [MonitoringLingkunganController::class, 'getPm25'])->name('monitoring.pm25');
    Route::get('/pm10', [MonitoringLingkunganController::class, 'getPm10'])->name('monitoring.pm10');
    Route::get('/eco2', [MonitoringLingkunganController::class, 'getEco2'])->name('monitoring.eco2');
    Route::get('/tvoc', [MonitoringLingkunganController::class, 'getTvoc'])->name('monitoring.tvoc');
    Route::get('/lokasi', [MonitoringLingkunganController::class, 'getLokasi'])->name('monitoring.lokasi');
    Route::get('/all', [MonitoringLingkunganController::class, 'getAll'])->name('monitoring.all');
    Route::get('/history', [MonitoringLingkunganController::class, 'getHistory'])->name('monitoring.history');
});

/**
 * ============================================
 * LISTRIK (ELECTRICITY) ENDPOINTS
 * ============================================
 * 12 Individual sensor endpoints + all + history
 */
Route::prefix('listrik')->group(function () {
    // Voltage Line-to-Line
    Route::get('/voltage-l1l2', [ListrikController::class, 'getVoltageL1L2'])->name('listrik.voltage_l1l2');
    Route::get('/voltage-l2l3', [ListrikController::class, 'getVoltageL2L3'])->name('listrik.voltage_l2l3');
    Route::get('/voltage-l3l1', [ListrikController::class, 'getVoltageL3L1'])->name('listrik.voltage_l3l1');

    // Voltage Line-to-Neutral
    Route::get('/voltage-l1n', [ListrikController::class, 'getVoltageL1N'])->name('listrik.voltage_l1n');
    Route::get('/voltage-l2n', [ListrikController::class, 'getVoltageL2N'])->name('listrik.voltage_l2n');
    Route::get('/voltage-l3n', [ListrikController::class, 'getVoltageL3N'])->name('listrik.voltage_l3n');

    // Current
    Route::get('/current-l1', [ListrikController::class, 'getCurrentL1'])->name('listrik.current_l1');
    Route::get('/current-l2', [ListrikController::class, 'getCurrentL2'])->name('listrik.current_l2');
    Route::get('/current-l3', [ListrikController::class, 'getCurrentL3'])->name('listrik.current_l3');
    Route::get('/current-n', [ListrikController::class, 'getCurrentN'])->name('listrik.current_n');

    // Frequency & Power Factor
    Route::get('/frecuency', [ListrikController::class, 'getFrecuency'])->name('listrik.frecuency');
    Route::get('/power-factor', [ListrikController::class, 'getPowerFactor'])->name('listrik.power_factor');

    // Combined
    Route::get('/all', [ListrikController::class, 'getAll'])->name('listrik.all');
    Route::get('/history', [ListrikController::class, 'getHistory'])->name('listrik.history');
});

/**
 * ============================================
 * DAYA (POWER) ENDPOINTS
 * ============================================
 * 9 Individual sensor endpoints + all + history
 */
Route::prefix('daya')->group(function () {
    // Active Power (Real Power)
    Route::get('/active-power-r', [DayaController::class, 'getActivePowerR'])->name('daya.active_power_r');
    Route::get('/active-power-s', [DayaController::class, 'getActivePowerS'])->name('daya.active_power_s');
    Route::get('/active-power-t', [DayaController::class, 'getActivePowerT'])->name('daya.active_power_t');

    // Reactive Power
    Route::get('/reactive-power-r', [DayaController::class, 'getReactivePowerR'])->name('daya.reactive_power_r');
    Route::get('/reactive-power-s', [DayaController::class, 'getReactivePowerS'])->name('daya.reactive_power_s');
    Route::get('/reactive-power-t', [DayaController::class, 'getReactivePowerT'])->name('daya.reactive_power_t');

    // Apparent Power
    Route::get('/apparent-power-r', [DayaController::class, 'getApparentPowerR'])->name('daya.apparent_power_r');
    Route::get('/apparent-power-s', [DayaController::class, 'getApparentPowerS'])->name('daya.apparent_power_s');
    Route::get('/apparent-power-t', [DayaController::class, 'getApparentPowerT'])->name('daya.apparent_power_t');

    // Combined
    Route::get('/all', [DayaController::class, 'getAll'])->name('daya.all');
    Route::get('/history', [DayaController::class, 'getHistory'])->name('daya.history');
});

/**
 * ============================================
 * WEBSOCKET BROADCAST CHANNELS
 * ============================================
 */
Route::get('/channels', function () {
    return response()->json([
        'status' => 'Broadcasting channels ready',
        'channels' => [
            'monitoring' => ['suhu', 'kelembapan', 'pm25', 'pm10', 'eco2', 'tvoc', 'lokasi'],
            'listrik' => ['voltage_l1l2', 'voltage_l2l3', 'voltage_l3l1', 'voltage_l1n', 'voltage_l2n', 'voltage_l3n', 'current_l1', 'current_l2', 'current_l3', 'current_n', 'frecuency', 'power_factor'],
            'daya' => ['active_power_r', 'active_power_s', 'active_power_t', 'reactive_power_r', 'reactive_power_s', 'reactive_power_t', 'apparent_power_r', 'apparent_power_s', 'apparent_power_t']
        ]
    ]);
})->name('api.channels');

/**
 * ============================================
 * API DOCUMENTATION
 * ============================================
 */
Route::get('/docs', function () {
    return response()->json([
        'title' => 'Monitoring Listrik & Lingkungan API WebSocket',
        'version' => '1.0.0',
        'description' => 'RESTful API with WebSocket real-time broadcasting for electricity and environmental monitoring',
        'base_url' => url('/api'),
        'total_endpoints' => 34,
        'endpoints' => [
            'monitoring' => [
                'GET /api/monitoring/suhu' => 'Get latest temperature data',
                'GET /api/monitoring/kelembapan' => 'Get latest humidity data',
                'GET /api/monitoring/pm25' => 'Get latest PM2.5 data',
                'GET /api/monitoring/pm10' => 'Get latest PM10 data',
                'GET /api/monitoring/eco2' => 'Get latest eCO2 data',
                'GET /api/monitoring/tvoc' => 'Get latest TVOC data',
                'GET /api/monitoring/lokasi' => 'Get location info',
                'GET /api/monitoring/all' => 'Get all monitoring data',
                'GET /api/monitoring/history' => 'Get monitoring history (last 100 records)',
            ],
            'listrik' => [
                'GET /api/listrik/voltage-l1l2' => 'Get L1-L2 line voltage',
                'GET /api/listrik/voltage-l2l3' => 'Get L2-L3 line voltage',
                'GET /api/listrik/voltage-l3l1' => 'Get L3-L1 line voltage',
                'GET /api/listrik/voltage-l1n' => 'Get L1 phase to neutral voltage',
                'GET /api/listrik/voltage-l2n' => 'Get L2 phase to neutral voltage',
                'GET /api/listrik/voltage-l3n' => 'Get L3 phase to neutral voltage',
                'GET /api/listrik/current-l1' => 'Get L1 phase current',
                'GET /api/listrik/current-l2' => 'Get L2 phase current',
                'GET /api/listrik/current-l3' => 'Get L3 phase current',
                'GET /api/listrik/current-n' => 'Get neutral current',
                'GET /api/listrik/frecuency' => 'Get system frequency',
                'GET /api/listrik/power-factor' => 'Get power factor',
                'GET /api/listrik/all' => 'Get all listrik data',
                'GET /api/listrik/history' => 'Get listrik history (last 100 records)',
            ],
            'daya' => [
                'GET /api/daya/active-power-r' => 'Get R phase active power',
                'GET /api/daya/active-power-s' => 'Get S phase active power',
                'GET /api/daya/active-power-t' => 'Get T phase active power',
                'GET /api/daya/reactive-power-r' => 'Get R phase reactive power',
                'GET /api/daya/reactive-power-s' => 'Get S phase reactive power',
                'GET /api/daya/reactive-power-t' => 'Get T phase reactive power',
                'GET /api/daya/apparent-power-r' => 'Get R phase apparent power',
                'GET /api/daya/apparent-power-s' => 'Get S phase apparent power',
                'GET /api/daya/apparent-power-t' => 'Get T phase apparent power',
                'GET /api/daya/all' => 'Get all daya data',
                'GET /api/daya/history' => 'Get daya history (last 100 records)',
            ]
        ],
        'broadcast_channels' => [
            'monitoring.suhu', 'monitoring.kelembapan', 'monitoring.pm25', 'monitoring.pm10', 'monitoring.eco2', 'monitoring.tvoc', 'monitoring.lokasi',
            'listrik.voltage.l1l2', 'listrik.voltage.l2l3', 'listrik.voltage.l3l1', 'listrik.voltage.l1n', 'listrik.voltage.l2n', 'listrik.voltage.l3n',
            'listrik.current.l1', 'listrik.current.l2', 'listrik.current.l3', 'listrik.current.n', 'listrik.frecuency', 'listrik.power_factor',
            'daya.active.r', 'daya.active.s', 'daya.active.t', 'daya.reactive.r', 'daya.reactive.s', 'daya.reactive.t',
            'daya.apparent.r', 'daya.apparent.s', 'daya.apparent.t'
        ],
        'websocket_config' => [
            'host' => env('REVERB_HOST'),
            'port' => env('REVERB_PORT'),
            'scheme' => env('REVERB_SCHEME'),
            'encrypted' => env('REVERB_SCHEME') === 'https'
        ]
    ]);
})->name('api.docs');
