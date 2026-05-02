<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\RsptdPowerBaru;
use App\Services\EnergyService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

use App\Events\ActivePowerRUpdated;
use App\Events\ActivePowerSUpdated;
use App\Events\ActivePowerTUpdated;

use App\Events\ReactivePowerRUpdated;
use App\Events\ReactivePowerSUpdated;
use App\Events\ReactivePowerTUpdated;

use App\Events\ApparentPowerRUpdated;
use App\Events\ApparentPowerSUpdated;
use App\Events\ApparentPowerTUpdated;

use App\Events\DataDayaUpdated;
use App\Events\BiayaDayaUpdated;
use App\Events\DayaHistoryUpdated;

use Carbon\Carbon;

class DayaController extends Controller
{
    private int $tarifPerKwh = 1260;

    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'active_power_r' => 'nullable|numeric',
            'active_power_s' => 'nullable|numeric',
            'active_power_t' => 'nullable|numeric',

            'reactive_power_r' => 'nullable|numeric',
            'reactive_power_s' => 'nullable|numeric',
            'reactive_power_t' => 'nullable|numeric',

            'apparent_power_r' => 'nullable|numeric',
            'apparent_power_s' => 'nullable|numeric',
            'apparent_power_t' => 'nullable|numeric',
        ]);

        if (empty(array_filter($validated, fn ($value) => $value !== null && $value !== ''))) {
            return response()->json([
                'success' => false,
                'message' => 'Minimal satu data daya harus diisi.',
                'field_yang_benar' => [
                    'active_power_r',
                    'active_power_s',
                    'active_power_t',
                    'reactive_power_r',
                    'reactive_power_s',
                    'reactive_power_t',
                    'apparent_power_r',
                    'apparent_power_s',
                    'apparent_power_t',
                ],
            ], 422);
        }

        $data = RsptdPowerBaru::create($validated);

        if (array_key_exists('active_power_r', $validated)) {
            event(new ActivePowerRUpdated($data->active_power_r));
        }

        if (array_key_exists('active_power_s', $validated)) {
            event(new ActivePowerSUpdated($data->active_power_s));
        }

        if (array_key_exists('active_power_t', $validated)) {
            event(new ActivePowerTUpdated($data->active_power_t));
        }

        if (array_key_exists('reactive_power_r', $validated)) {
            event(new ReactivePowerRUpdated($data->reactive_power_r));
        }

        if (array_key_exists('reactive_power_s', $validated)) {
            event(new ReactivePowerSUpdated($data->reactive_power_s));
        }

        if (array_key_exists('reactive_power_t', $validated)) {
            event(new ReactivePowerTUpdated($data->reactive_power_t));
        }

        if (array_key_exists('apparent_power_r', $validated)) {
            event(new ApparentPowerRUpdated($data->apparent_power_r));
        }

        if (array_key_exists('apparent_power_s', $validated)) {
            event(new ApparentPowerSUpdated($data->apparent_power_s));
        }

        if (array_key_exists('apparent_power_t', $validated)) {
            event(new ApparentPowerTUpdated($data->apparent_power_t));
        }

        $totalActivePower = EnergyService::totalActivePowerWatt($data);
        $biayaRealtime = $this->hitungRealtime();

        event(new DataDayaUpdated([
            'id' => $data->id,
            'active_power_r' => $data->active_power_r,
            'active_power_s' => $data->active_power_s,
            'active_power_t' => $data->active_power_t,
            'reactive_power_r' => $data->reactive_power_r,
            'reactive_power_s' => $data->reactive_power_s,
            'reactive_power_t' => $data->reactive_power_t,
            'apparent_power_r' => $data->apparent_power_r,
            'apparent_power_s' => $data->apparent_power_s,
            'apparent_power_t' => $data->apparent_power_t,
            'total_active_power_watt' => round($totalActivePower, 2),
            'time_stamp' => $data->time_stamp,
        ]));

        event(new BiayaDayaUpdated($biayaRealtime));

        event(new DayaHistoryUpdated([
            'id' => $data->id,
            'timestamp' => $this->formatTimestamp($data->time_stamp),

            'active_power_r' => $data->active_power_r,
            'active_power_s' => $data->active_power_s,
            'active_power_t' => $data->active_power_t,

            'reactive_power_r' => $data->reactive_power_r,
            'reactive_power_s' => $data->reactive_power_s,
            'reactive_power_t' => $data->reactive_power_t,

            'apparent_power_r' => $data->apparent_power_r,
            'apparent_power_s' => $data->apparent_power_s,
            'apparent_power_t' => $data->apparent_power_t,

            'total_active_power' => round($totalActivePower, 2),

            'energy_kwh' => $biayaRealtime['energy_kwh'] ?? 0,
            'biaya' => $biayaRealtime['biaya'] ?? 0,
            'tarif_per_kwh' => $biayaRealtime['tarif_per_kwh'] ?? $this->tarifPerKwh,
        ]));

        return response()->json([
            'success' => true,
            'message' => 'Data daya berhasil disimpan dan dibroadcast',
            'data' => $data,
            'total_active_power_watt' => round($totalActivePower, 2),
            'biaya_realtime' => $biayaRealtime,
        ], 201);
    }

    public function getActivePowerR(): JsonResponse
    {
        return $this->latestSensor('active_power_r', 'W');
    }

    public function getActivePowerS(): JsonResponse
    {
        return $this->latestSensor('active_power_s', 'W');
    }

    public function getActivePowerT(): JsonResponse
    {
        return $this->latestSensor('active_power_t', 'W');
    }

    public function getReactivePowerR(): JsonResponse
    {
        return $this->latestSensor('reactive_power_r', 'VAR');
    }

    public function getReactivePowerS(): JsonResponse
    {
        return $this->latestSensor('reactive_power_s', 'VAR');
    }

    public function getReactivePowerT(): JsonResponse
    {
        return $this->latestSensor('reactive_power_t', 'VAR');
    }

    public function getApparentPowerR(): JsonResponse
    {
        return $this->latestSensor('apparent_power_r', 'VA');
    }

    public function getApparentPowerS(): JsonResponse
    {
        return $this->latestSensor('apparent_power_s', 'VA');
    }

    public function getApparentPowerT(): JsonResponse
    {
        return $this->latestSensor('apparent_power_t', 'VA');
    }

    public function getTotalActivePower(): JsonResponse
    {
        $data = RsptdPowerBaru::orderBy('time_stamp', 'desc')->first();

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data daya belum tersedia',
                'sensor' => 'total_active_power',
                'value' => 0,
                'unit' => 'W',
            ], 404);
        }

        $total = EnergyService::totalActivePowerWatt($data);

        return response()->json([
            'success' => true,
            'sensor' => 'total_active_power',
            'value' => round($total, 2),
            'unit' => 'W',
            'timestamp' => $data->time_stamp,
            'detail' => [
                'active_power_r' => $data->active_power_r,
                'active_power_s' => $data->active_power_s,
                'active_power_t' => $data->active_power_t,
            ],
        ]);
    }

    public function getAll(): JsonResponse
    {
        $data = RsptdPowerBaru::orderBy('time_stamp', 'desc')->first();

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data daya belum tersedia',
                'data' => null,
            ], 404);
        }

        $totalActivePower = EnergyService::totalActivePowerWatt($data);
        $biayaRealtime = $this->hitungRealtime();

        return response()->json([
            'success' => true,
            'message' => 'Data daya terbaru berhasil diambil',
            'data' => $data,
            'total_active_power' => [
                'value' => round($totalActivePower, 2),
                'unit' => 'W',
            ],
            'biaya_realtime' => $biayaRealtime,
        ]);
    }

    public function getHistory(Request $request): JsonResponse
    {
        $allowedSensors = [
            'active_power_r',
            'active_power_s',
            'active_power_t',
            'reactive_power_r',
            'reactive_power_s',
            'reactive_power_t',
            'apparent_power_r',
            'apparent_power_s',
            'apparent_power_t',
            'total_active_power',
        ];

        $sensor = $request->query('sensor');
        $periode = $request->query('periode');
        $from = $request->query('from');
        $to = $request->query('to');
        $limit = (int) $request->query('limit', 100);

        if ($limit <= 0) {
            $limit = 100;
        }

        if ($limit > 1000) {
            $limit = 1000;
        }

        if ($sensor && !in_array($sensor, $allowedSensors, true)) {
            return response()->json([
                'success' => false,
                'message' => 'Sensor daya tidak valid',
                'allowed_sensors' => $allowedSensors,
            ], 422);
        }

        $query = RsptdPowerBaru::query();

        $this->applyPeriodeFilter($query, $periode);

        if ($from) {
            $query->where('time_stamp', '>=', $from);
        }

        if ($to) {
            $query->where('time_stamp', '<=', $to);
        }

        $data = $query->orderBy('time_stamp', 'desc')
            ->limit($limit)
            ->get()
            ->sortByDesc('time_stamp')
            ->values();

        if ($sensor) {
            $data = $data->map(function ($item) use ($sensor) {
                $value = $sensor === 'total_active_power'
                    ? EnergyService::totalActivePowerWatt($item)
                    : $item->{$sensor};

                return [
                    'id' => $item->id,
                    'sensor' => $sensor,
                    'value' => round((float) $value, 4),
                    'time_stamp' => $item->time_stamp,
                ];
            });
        }

        return response()->json([
            'success' => true,
            'message' => 'History daya berhasil diambil',
            'sensor' => $sensor ?? 'all',
            'periode' => $periode,
            'from' => $from,
            'to' => $to,
            'limit' => $limit,
            'total' => $data->count(),
            'data' => $data,
        ]);
    }

    public function getTotalEnergi(Request $request): JsonResponse
    {
        $periode = $request->query('periode', 'hari');
        $tarif = (int) $request->query('tarif', $this->tarifPerKwh);
        $from = $request->query('from');
        $to = $request->query('to');

        $query = RsptdPowerBaru::query();

        $this->applyPeriodeFilter($query, $periode);

        if ($from) {
            $query->where('time_stamp', '>=', $from);
        }

        if ($to) {
            $query->where('time_stamp', '<=', $to);
        }

        $records = $query->orderBy('time_stamp', 'asc')->get();

        $energyKwh = EnergyService::calculateKwhFromRecords($records);
        $biaya = EnergyService::calculateCost($energyKwh, $tarif);

        return response()->json([
            'success' => true,
            'message' => 'Total energi berhasil dihitung',
            'periode' => $periode,
            'from' => $from,
            'to' => $to,
            'total_data' => $records->count(),
            'energy_kwh' => round($energyKwh, 6),
            'tarif_per_kwh' => $tarif,
            'estimasi_biaya' => round($biaya, 2),
            'unit_energy' => 'kWh',
            'unit_biaya' => 'Rp',
            'rumus' => 'kWh = (rata-rata total active power watt / 1000) x (selisih waktu detik / 3600)',
        ]);
    }

    public function getEstimasi(Request $request): JsonResponse
    {
        $type = $request->query('type', 'hari');
        $tarif = (int) $request->query('tarif', $this->tarifPerKwh);
        $from = $request->query('from');
        $to = $request->query('to');

        $query = RsptdPowerBaru::query();

        $this->applyPeriodeFilter($query, $type);

        if ($from) {
            $query->where('time_stamp', '>=', $from);
        }

        if ($to) {
            $query->where('time_stamp', '<=', $to);
        }

        $records = $query->orderBy('time_stamp', 'asc')->get();

        $energyKwh = EnergyService::calculateKwhFromRecords($records);
        $biaya = EnergyService::calculateCost($energyKwh, $tarif);

        return response()->json([
            'success' => true,
            'message' => 'Estimasi energi dan biaya berhasil dihitung',
            'type' => $type,
            'from' => $from,
            'to' => $to,
            'total_data' => $records->count(),
            'energy_kwh' => round($energyKwh, 6),
            'tarif_per_kwh' => $tarif,
            'estimasi_biaya' => round($biaya, 2),
            'unit_energy' => 'kWh',
            'unit_biaya' => 'Rp',
            'rumus' => 'biaya = energy_kwh x tarif_per_kwh',
        ]);
    }

    private function latestSensor(string $sensor, string $unit): JsonResponse
    {
        $data = RsptdPowerBaru::whereNotNull($sensor)
            ->orderBy('time_stamp', 'desc')
            ->first();

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data daya belum tersedia',
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
            'timestamp' => $data->time_stamp,
        ]);
    }

    private function hitungRealtime(): array
    {
        $records = RsptdPowerBaru::orderBy('time_stamp', 'desc')
            ->take(2)
            ->get()
            ->sortBy('time_stamp')
            ->values();

        return EnergyService::calculateRealtimeFromLastTwoRecords($records);
    }

    private function applyPeriodeFilter($query, ?string $periode): void
    {
        if ($periode === 'hari') {
            $query->whereDate('time_stamp', now()->toDateString());
        } elseif ($periode === 'minggu') {
            $query->whereBetween('time_stamp', [
                now()->copy()->startOfWeek(),
                now()->copy()->endOfWeek(),
            ]);
        } elseif ($periode === 'bulan') {
            $query->whereYear('time_stamp', now()->year)
                ->whereMonth('time_stamp', now()->month);
        } elseif ($periode === 'tahun') {
            $query->whereYear('time_stamp', now()->year);
        }
    }

    private function formatTimestamp($timestamp): ?string
    {
        if (!$timestamp) {
            return null;
        }

        return Carbon::parse($timestamp)->format('Y-m-d H:i:s');
    }
}