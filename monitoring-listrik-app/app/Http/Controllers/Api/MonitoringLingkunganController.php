<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\MonitoringLingkungan;
use App\Services\NotificationService;
use App\Services\IEQService;

use App\Events\SuhuUpdated;
use App\Events\KelembapanUpdated;
use App\Events\Pm25Updated;
use App\Events\Pm10Updated;
use App\Events\GasCoUpdated;
use App\Events\GasCo2Updated;
use App\Events\CahayaUpdated;
use App\Events\KebisinganUpdated;
use App\Events\LokasiUpdated;
use App\Events\IEQUpdated;

use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class MonitoringLingkunganController extends Controller
{
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'suhu' => 'nullable|numeric',
            'kelembapan' => 'nullable|numeric',
            'pm25' => 'nullable|numeric',
            'pm10' => 'nullable|numeric',
            'gas_co' => 'nullable|numeric',
            'gas_co2' => 'nullable|numeric',
            'cahaya' => 'nullable|numeric',
            'kebisingan' => 'nullable|numeric',
            'lokasi' => 'nullable|string|max:255',
        ]);

        $data = MonitoringLingkungan::create($validated);

        $ieq = IEQService::calculate($data);

        event(new IEQUpdated($ieq));

        event(new SuhuUpdated($data->suhu));
        event(new KelembapanUpdated($data->kelembapan));
        event(new Pm25Updated($data->pm25));
        event(new Pm10Updated($data->pm10));
        event(new GasCoUpdated($data->gas_co));
        event(new GasCo2Updated($data->gas_co2));
        event(new CahayaUpdated($data->cahaya));
        event(new KebisinganUpdated($data->kebisingan));
        event(new LokasiUpdated($data->lokasi));

        $notifications = $this->getAllNotifications($data);
        $createdNotifications = NotificationService::createMany($notifications);

        return response()->json([
            'success' => true,
            'message' => 'Data monitoring lingkungan berhasil disimpan dan dibroadcast',
            'data' => $data,
            'ieq' => $ieq,
            'notifications' => $createdNotifications,
        ], 201);
    }

    public function getIEQ(): JsonResponse
    {
        $data = MonitoringLingkungan::latest()->first();

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data monitoring lingkungan belum tersedia',
                'ieq' => null,
            ], 404);
        }

        $ieq = IEQService::calculate($data);

        return response()->json([
            'success' => true,
            'message' => 'Status IEQ berhasil diambil',
            'data' => $data,
            'ieq' => $ieq,
        ]);
    }

    private function notif($sensor, $value, $unit, $level, $message, $timestamp = null, $lokasi = null): array
    {
        return [
            'level' => $level,
            'status' => 'aktif',
            'kategori' => 'lingkungan',
            'sensor' => $sensor,
            'title' => ucfirst($level),
            'value' => $value,
            'unit' => $unit,
            'message' => $message,
            'lokasi' => $lokasi ?? 'Ruang Admin',
            'timestamp' => $timestamp,
        ];
    }

    private function getNotification($sensor, $value, $timestamp = null, $lokasi = null): ?array
    {
        if ($value === null) return null;

        $value = (float) $value;

        return match ($sensor) {
            'suhu' => $value < 20
                ? $this->notif('suhu', $value, '°C', 'waspada', 'Suhu di bawah normal', $timestamp, $lokasi)
                : ($value > 29
                    ? $this->notif('suhu', $value, '°C', 'bahaya', 'Suhu terlalu tinggi!', $timestamp, $lokasi)
                    : null),

            'kelembapan' => $value < 40
                ? $this->notif('kelembapan', $value, '%', 'waspada', 'Kelembapan rendah', $timestamp, $lokasi)
                : ($value > 60
                    ? $this->notif('kelembapan', $value, '%', 'bahaya', 'Kelembapan tinggi!', $timestamp, $lokasi)
                    : null),

            'pm25' => $value > 35
                ? $this->notif('pm25', $value, 'µg/m³', 'bahaya', 'PM2.5 tinggi!', $timestamp, $lokasi)
                : null,

            'pm10' => $value > 70
                ? $this->notif('pm10', $value, 'µg/m³', 'bahaya', 'PM10 tinggi!', $timestamp, $lokasi)
                : null,

            'gas_co' => $value > 10000
                ? $this->notif('gas_co', $value, 'ppm', 'bahaya', 'Gas CO berbahaya!', $timestamp, $lokasi)
                : null,

            'gas_co2' => $value > 1
                ? $this->notif('gas_co2', $value, 'ppm', 'bahaya', 'Gas CO2 tinggi!', $timestamp, $lokasi)
                : null,

            'cahaya' => $value < 100
                ? $this->notif('cahaya', $value, 'lux', 'waspada', 'Cahaya kurang', $timestamp, $lokasi)
                : null,

            'kebisingan' => $value > 45
                ? $this->notif('kebisingan', $value, 'dB', 'bahaya', 'Kebisingan tinggi!', $timestamp, $lokasi)
                : null,

            default => null,
        };
    }

    private function getAllNotifications($data): array
    {
        $timestamp = $data?->waktu;
        $lokasi = $data?->lokasi ?? 'Ruang Admin';

        return array_values(array_filter([
            $this->getNotification('suhu', $data?->suhu, $timestamp, $lokasi),
            $this->getNotification('kelembapan', $data?->kelembapan, $timestamp, $lokasi),
            $this->getNotification('pm25', $data?->pm25, $timestamp, $lokasi),
            $this->getNotification('pm10', $data?->pm10, $timestamp, $lokasi),
            $this->getNotification('gas_co', $data?->gas_co, $timestamp, $lokasi),
            $this->getNotification('gas_co2', $data?->gas_co2, $timestamp, $lokasi),
            $this->getNotification('cahaya', $data?->cahaya, $timestamp, $lokasi),
            $this->getNotification('kebisingan', $data?->kebisingan, $timestamp, $lokasi),
        ]));
    }
}