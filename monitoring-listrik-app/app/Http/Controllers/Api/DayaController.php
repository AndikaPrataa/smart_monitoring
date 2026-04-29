<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\RsptdPowerBaru;
use App\Events\ActivePowerRUpdated;
use App\Events\ActivePowerSUpdated;
use App\Events\ActivePowerTUpdated;
use App\Events\ReactivePowerRUpdated;
use App\Events\ReactivePowerSUpdated;
use App\Events\ReactivePowerTUpdated;
use App\Events\ApparentPowerRUpdated;
use App\Events\ApparentPowerSUpdated;
use App\Events\ApparentPowerTUpdated;
use App\Events\BiayaDayaUpdated;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class DayaController extends Controller
{
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

        $data = RsptdPowerBaru::create($validated);

        event(new ActivePowerRUpdated($data->active_power_r));
        event(new ActivePowerSUpdated($data->active_power_s));
        event(new ActivePowerTUpdated($data->active_power_t));
        event(new ReactivePowerRUpdated($data->reactive_power_r));
        event(new ReactivePowerSUpdated($data->reactive_power_s));
        event(new ReactivePowerTUpdated($data->reactive_power_t));
        event(new ApparentPowerRUpdated($data->apparent_power_r));
        event(new ApparentPowerSUpdated($data->apparent_power_s));
        event(new ApparentPowerTUpdated($data->apparent_power_t));

        $biayaRealtime = $this->hitungBiayaRealtime();
        event(new BiayaDayaUpdated($biayaRealtime));

        return response()->json([
            'success' => true,
            'message' => 'Data daya berhasil disimpan dan dibroadcast',
            'data' => $data,
            'biaya_realtime' => $biayaRealtime,
        ], 201);
    }
public function getTotalActivePower(): JsonResponse
{
    $data = RsptdPowerBaru::latest('time_stamp')->first();

    $totalActivePower =
        (float) ($data?->active_power_r ?? 0) +
        (float) ($data?->active_power_s ?? 0) +
        (float) ($data?->active_power_t ?? 0);

    return response()->json([
        'sensor' => 'total_active_power',
        'unit' => 'W',
        'value' => round($totalActivePower, 2),
        'timestamp' => $data?->time_stamp,
        'detail' => 'Total active power R + S + T',
    ]);
}
    private function hitungEnergiDanBiaya($query): array
    {
        $tarif = 1226;
        $records = $query->get();

        $totalBiaya = 0;
        $totalKwh = 0;

        foreach ($records as $row) {
            $totalWatt =
                (float) ($row->active_power_r ?? 0) +
                (float) ($row->active_power_s ?? 0) +
                (float) ($row->active_power_t ?? 0);

            $kwh = $totalWatt / 1000;
            $biaya = $kwh * $tarif;

            $totalKwh += $kwh;
            $totalBiaya += $biaya;
        }

        return [
            'total_kwh' => round($totalKwh, 4),
            'total_biaya' => round($totalBiaya),
            'jumlah_data' => $records->count(),
            'tarif' => $tarif,
        ];
    }

    private function hitungBiayaRealtime(): array
    {
        return [
            'type' => 'biaya_daya_realtime',
            'periode' => now()->format('Y-m-d'),
            'data' => $this->hitungEnergiDanBiaya(
                RsptdPowerBaru::whereDate('time_stamp', now()->toDateString())
            ),
        ];
    }

    public function getEstimasiHarian(): JsonResponse
    {
        return response()->json([
            'success' => true,
            'type' => 'estimasi_harian',
            'periode' => now()->format('Y-m-d'),
            'data' => $this->hitungEnergiDanBiaya(
                RsptdPowerBaru::whereDate('time_stamp', now()->toDateString())
            ),
        ]);
    }

    public function getEstimasiBulanan(): JsonResponse
    {
        return response()->json([
            'success' => true,
            'type' => 'estimasi_bulanan',
            'periode' => now()->format('Y-m'),
            'data' => $this->hitungEnergiDanBiaya(
                RsptdPowerBaru::whereYear('time_stamp', now()->year)
                    ->whereMonth('time_stamp', now()->month)
            ),
        ]);
    }

    public function getEstimasiTahunan(): JsonResponse
    {
        return response()->json([
            'success' => true,
            'type' => 'estimasi_tahunan',
            'periode' => now()->format('Y'),
            'data' => $this->hitungEnergiDanBiaya(
                RsptdPowerBaru::whereYear('time_stamp', now()->year)
            ),
        ]);
    }

    public function getActivePowerR(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'active_power_r',
            'unit' => 'W',
            'value' => $data?->active_power_r,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getActivePowerS(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'active_power_s',
            'unit' => 'W',
            'value' => $data?->active_power_s,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getActivePowerT(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'active_power_t',
            'unit' => 'W',
            'value' => $data?->active_power_t,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getReactivePowerR(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'reactive_power_r',
            'unit' => 'VAR',
            'value' => $data?->reactive_power_r,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getReactivePowerS(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'reactive_power_s',
            'unit' => 'VAR',
            'value' => $data?->reactive_power_s,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getReactivePowerT(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'reactive_power_t',
            'unit' => 'VAR',
            'value' => $data?->reactive_power_t,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getApparentPowerR(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'apparent_power_r',
            'unit' => 'VA',
            'value' => $data?->apparent_power_r,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getApparentPowerS(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'apparent_power_s',
            'unit' => 'VA',
            'value' => $data?->apparent_power_s,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getApparentPowerT(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'apparent_power_t',
            'unit' => 'VA',
            'value' => $data?->apparent_power_t,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getAll(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();

        return response()->json([
            'type' => 'daya',
            'timestamp' => $data?->time_stamp,
            'data' => $data,
            'biaya_realtime' => $this->hitungBiayaRealtime(),
        ]);
    }

    public function getHistory(): JsonResponse
    {
        $data = RsptdPowerBaru::orderBy('time_stamp', 'desc')->limit(100)->get();

        return response()->json([
            'type' => 'daya_history',
            'total' => $data->count(),
            'data' => $data,
        ]);
    }
}