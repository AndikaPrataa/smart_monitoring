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
use App\Events\TvocUpdated;
use App\Events\CahayaUpdated;
use App\Events\KebisinganUpdated;
use App\Events\LokasiUpdated;
use App\Events\IEQUpdated;
use App\Events\MonitoringHistoryUpdated;

use Carbon\Carbon;
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
            'tvoc' => 'nullable|numeric',
            'cahaya' => 'nullable|numeric',
            'kebisingan' => 'nullable|numeric',
            'lokasi' => 'nullable|string|max:255',
        ]);

        if (empty(array_filter($validated, fn ($value) => $value !== null && $value !== ''))) {
            return response()->json([
                'success' => false,
                'message' => 'Minimal satu data sensor harus diisi',
            ], 422);
        }

        $data = MonitoringLingkungan::create($validated);

        $ieq = IEQService::calculate($data);
        $tvocAverage8Hours = $this->getTvocAverageLast8Hours();

        event(new SuhuUpdated($data->suhu));
        event(new KelembapanUpdated($data->kelembapan));
        event(new Pm25Updated($data->pm25));
        event(new Pm10Updated($data->pm10));
        event(new GasCoUpdated($data->gas_co));
        event(new GasCo2Updated($data->gas_co2));
        event(new TvocUpdated($data->tvoc));
        event(new CahayaUpdated($data->cahaya));
        event(new KebisinganUpdated($data->kebisingan));
        event(new LokasiUpdated($data->lokasi));
        event(new IEQUpdated($ieq));

        event(new MonitoringHistoryUpdated([
            'id' => $data->id,
            'timestamp' => $this->formatTimestamp($data->waktu),
            'lokasi' => $data->lokasi,

            'suhu' => $data->suhu,
            'kelembapan' => $data->kelembapan,
            'pm25' => $data->pm25,
            'pm10' => $data->pm10,
            'gas_co' => $data->gas_co,
            'gas_co2' => $data->gas_co2,
            'tvoc' => $data->tvoc,
            'cahaya' => $data->cahaya,
            'kebisingan' => $data->kebisingan,

            'ieq' => $ieq,
            'tvoc_average_8_hours' => $tvocAverage8Hours,
        ]));

        $notifications = $this->getAllNotifications($data);
        $createdNotifications = NotificationService::createMany($notifications);

        return response()->json([
            'success' => true,
            'message' => 'Data monitoring lingkungan berhasil disimpan dan dibroadcast',
            'data' => $data,
            'ieq' => $ieq,
            'tvoc_average_8_hours' => $tvocAverage8Hours,
            'notifications' => $createdNotifications,
        ], 201);
    }

    public function getSuhu(): JsonResponse
    {
        return $this->latestSensor('suhu', '°C');
    }

    public function getKelembapan(): JsonResponse
    {
        return $this->latestSensor('kelembapan', '%');
    }

    public function getPm25(): JsonResponse
    {
        return $this->latestSensor('pm25', 'µg/m³');
    }

    public function getPm10(): JsonResponse
    {
        return $this->latestSensor('pm10', 'µg/m³');
    }

    public function getGasCo(): JsonResponse
    {
        return $this->latestSensor('gas_co', 'ppm');
    }

    public function getGasCo2(): JsonResponse
    {
        return $this->latestSensor('gas_co2', 'ppm');
    }

    public function getTvoc(): JsonResponse
    {
        $latest = MonitoringLingkungan::whereNotNull('tvoc')
            ->orderBy('waktu', 'desc')
            ->first();

        $average8Hours = $this->getTvocAverageLast8Hours();

        if (!$latest) {
            return response()->json([
                'success' => false,
                'message' => 'Data TVOC belum tersedia',
                'sensor' => 'tvoc',
                'value' => null,
                'average_8_hours' => $average8Hours,
                'unit' => 'ppm',
            ], 404);
        }

        return response()->json([
            'success' => true,
            'sensor' => 'tvoc',
            'value' => $latest->tvoc,
            'average_8_hours' => $average8Hours,
            'max_normal_average_8_hours' => 3,
            'unit' => 'ppm',
            'status' => $average8Hours !== null && $average8Hours > 3 ? 'BAHAYA' : 'NORMAL',
            'lokasi' => $latest->lokasi,
            'waktu' => $latest->waktu,
        ]);
    }

    public function getCahaya(): JsonResponse
    {
        return $this->latestSensor('cahaya', 'lux');
    }

    public function getKebisingan(): JsonResponse
    {
        return $this->latestSensor('kebisingan', 'dB');
    }

    public function getLokasi(): JsonResponse
    {
        return $this->latestSensor('lokasi', null);
    }

    public function getAll(): JsonResponse
    {
        $data = MonitoringLingkungan::orderBy('waktu', 'desc')->first();

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data monitoring lingkungan belum tersedia',
                'data' => null,
            ], 404);
        }

        $ieq = IEQService::calculate($data);

        return response()->json([
            'success' => true,
            'message' => 'Data monitoring lingkungan terbaru berhasil diambil',
            'data' => $data,
            'ieq' => $ieq,
            'tvoc_average_8_hours' => $this->getTvocAverageLast8Hours(),
        ]);
    }

    public function getHistory(Request $request): JsonResponse
    {
        $allowedSensors = [
            'suhu',
            'kelembapan',
            'pm25',
            'pm10',
            'gas_co',
            'gas_co2',
            'tvoc',
            'cahaya',
            'kebisingan',
            'lokasi',
        ];

        $sensor = $request->query('sensor');
        $limit = (int) $request->query('limit', 100);
        $periode = $request->query('periode');
        $from = $request->query('from');
        $to = $request->query('to');

        if ($limit <= 0) {
            $limit = 100;
        }

        if ($limit > 1000) {
            $limit = 1000;
        }

        if ($sensor && !in_array($sensor, $allowedSensors, true)) {
            return response()->json([
                'success' => false,
                'message' => 'Sensor tidak valid',
                'allowed_sensors' => $allowedSensors,
            ], 422);
        }

        $query = MonitoringLingkungan::query();

        if ($periode === 'hari') {
            $query->whereDate('waktu', now()->toDateString());
        } elseif ($periode === 'minggu') {
            $query->whereBetween('waktu', [
                now()->copy()->startOfWeek(),
                now()->copy()->endOfWeek(),
            ]);
        } elseif ($periode === 'bulan') {
            $query->whereYear('waktu', now()->year)
                ->whereMonth('waktu', now()->month);
        } elseif ($periode === 'tahun') {
            $query->whereYear('waktu', now()->year);
        }

        if ($from) {
            $query->where('waktu', '>=', $from);
        }

        if ($to) {
            $query->where('waktu', '<=', $to);
        }

        if ($sensor) {
            $query->whereNotNull($sensor);
        }

        $data = $query->orderBy('waktu', 'desc')
            ->limit($limit)
            ->get()
            ->sortByDesc('waktu')
            ->values();

        if ($sensor) {
            $data = $data->map(function ($item) use ($sensor) {
                return [
                    'id' => $item->id,
                    'sensor' => $sensor,
                    'value' => $item->{$sensor},
                    'lokasi' => $item->lokasi,
                    'waktu' => $item->waktu,
                ];
            });
        }

        return response()->json([
            'success' => true,
            'message' => 'History monitoring lingkungan berhasil diambil',
            'sensor' => $sensor ?? 'all',
            'periode' => $periode,
            'limit' => $limit,
            'total' => $data->count(),
            'data' => $data,
        ]);
    }

    public function getIEQ(): JsonResponse
    {
        $data = MonitoringLingkungan::orderBy('waktu', 'desc')->first();

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
            'tvoc_average_8_hours' => $this->getTvocAverageLast8Hours(),
        ]);
    }

    private function latestSensor(string $sensor, ?string $unit = null): JsonResponse
    {
        $data = MonitoringLingkungan::whereNotNull($sensor)
            ->orderBy('waktu', 'desc')
            ->first();

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data monitoring lingkungan belum tersedia',
                'sensor' => $sensor,
                'value' => null,
                'unit' => $unit,
            ], 404);
        }

        return response()->json([
            'success' => true,
            'sensor' => $sensor,
            'value' => $data->{$sensor},
            'unit' => $unit,
            'lokasi' => $data->lokasi,
            'waktu' => $data->waktu,
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
        if ($value === null) {
            return null;
        }

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

            'gas_co2' => $value > 1000
                ? $this->notif('gas_co2', $value, 'ppm', 'bahaya', 'Gas CO2 tinggi!', $timestamp, $lokasi)
                : null,

            'tvoc' => $value > 3
                ? $this->notif('tvoc', $value, 'ppm', 'bahaya', 'Rata-rata TVOC 8 jam melebihi batas normal 3 ppm.', $timestamp, $lokasi)
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
        $tvocAverage8Hours = $this->getTvocAverageLast8Hours();

        return array_values(array_filter([
            $this->getNotification('suhu', $data?->suhu, $timestamp, $lokasi),
            $this->getNotification('kelembapan', $data?->kelembapan, $timestamp, $lokasi),
            $this->getNotification('pm25', $data?->pm25, $timestamp, $lokasi),
            $this->getNotification('pm10', $data?->pm10, $timestamp, $lokasi),
            $this->getNotification('gas_co', $data?->gas_co, $timestamp, $lokasi),
            $this->getNotification('gas_co2', $data?->gas_co2, $timestamp, $lokasi),
            $this->getNotification('tvoc', $tvocAverage8Hours, $timestamp, $lokasi),
            $this->getNotification('cahaya', $data?->cahaya, $timestamp, $lokasi),
            $this->getNotification('kebisingan', $data?->kebisingan, $timestamp, $lokasi),
        ]));
    }

    private function getTvocAverageLast8Hours(): ?float
    {
        $average = MonitoringLingkungan::whereNotNull('tvoc')
            ->where('waktu', '>=', now()->subHours(8))
            ->avg('tvoc');

        if ($average === null) {
            return null;
        }

        return round((float) $average, 3);
    }

    private function formatTimestamp($timestamp): ?string
    {
        if (!$timestamp) {
            return null;
        }

        return Carbon::parse($timestamp)->format('Y-m-d H:i:s');
    }
}