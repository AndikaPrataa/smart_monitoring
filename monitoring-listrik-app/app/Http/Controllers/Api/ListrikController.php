<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\RsptdDataBaru;
use App\Services\NotificationService;

use App\Events\VoltageL1L2Updated;
use App\Events\VoltageL2L3Updated;
use App\Events\VoltageL3L1Updated;
use App\Events\VoltageL1NUpdated;
use App\Events\VoltageL2NUpdated;
use App\Events\VoltageL3NUpdated;
use App\Events\CurrentL1Updated;
use App\Events\CurrentL2Updated;
use App\Events\CurrentL3Updated;
use App\Events\CurrentNUpdated;
use App\Events\FrecuencyUpdated;
use App\Events\PowerFactorUpdated;
use App\Events\TotalVoltageUpdated;
use App\Events\TotalCurrentUpdated;
use App\Events\ListrikHistoryUpdated;

use Carbon\Carbon;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class ListrikController extends Controller
{
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'voltage_l1l2' => 'nullable|numeric',
            'voltage_l2l3' => 'nullable|numeric',
            'voltage_l3l1' => 'nullable|numeric',
            'voltage_l1n' => 'nullable|numeric',
            'voltage_l2n' => 'nullable|numeric',
            'voltage_l3n' => 'nullable|numeric',
            'current_l1' => 'nullable|numeric',
            'current_l2' => 'nullable|numeric',
            'current_l3' => 'nullable|numeric',
            'current_n' => 'nullable|numeric',
            'frecuency' => 'nullable|numeric',
            'power_factor' => 'nullable|numeric',
        ]);

        if (empty(array_filter($validated, fn ($value) => $value !== null && $value !== ''))) {
            return response()->json([
                'success' => false,
                'message' => 'Minimal satu data listrik harus diisi.',
            ], 422);
        }

        $data = RsptdDataBaru::create($validated);

        event(new VoltageL1L2Updated($data->voltage_l1l2));
        event(new VoltageL2L3Updated($data->voltage_l2l3));
        event(new VoltageL3L1Updated($data->voltage_l3l1));
        event(new VoltageL1NUpdated($data->voltage_l1n));
        event(new VoltageL2NUpdated($data->voltage_l2n));
        event(new VoltageL3NUpdated($data->voltage_l3n));

        event(new CurrentL1Updated($data->current_l1));
        event(new CurrentL2Updated($data->current_l2));
        event(new CurrentL3Updated($data->current_l3));
        event(new CurrentNUpdated($data->current_n));

        event(new FrecuencyUpdated($data->frecuency));
        event(new PowerFactorUpdated($data->power_factor));

        $totalVoltage = collect([
            $data->voltage_l1n,
            $data->voltage_l2n,
            $data->voltage_l3n,
        ])->filter(fn ($value) => $value !== null)->avg();

        $totalCurrent = collect([
            $data->current_l1,
            $data->current_l2,
            $data->current_l3,
        ])->filter(fn ($value) => $value !== null)->sum();

        $totalVoltage = round((float) $totalVoltage, 2);
        $totalCurrent = round((float) $totalCurrent, 2);

        event(new TotalVoltageUpdated($totalVoltage));
        event(new TotalCurrentUpdated($totalCurrent));

        event(new ListrikHistoryUpdated([
            'id' => $data->id,
            'timestamp' => $this->formatTimestamp($data->time_stamp),

            'voltage_l1l2' => $data->voltage_l1l2,
            'voltage_l2l3' => $data->voltage_l2l3,
            'voltage_l3l1' => $data->voltage_l3l1,

            'voltage_l1n' => $data->voltage_l1n,
            'voltage_l2n' => $data->voltage_l2n,
            'voltage_l3n' => $data->voltage_l3n,

            'current_l1' => $data->current_l1,
            'current_l2' => $data->current_l2,
            'current_l3' => $data->current_l3,
            'current_n' => $data->current_n,

            'frecuency' => $data->frecuency,
            'power_factor' => $data->power_factor,

            'total_voltage' => $totalVoltage,
            'total_current' => $totalCurrent,
        ]));

        $notifications = $this->getAllNotifications($data, $totalVoltage, $totalCurrent);
        $createdNotifications = NotificationService::createMany($notifications);

        return response()->json([
            'success' => true,
            'message' => 'Data listrik berhasil disimpan dan dibroadcast',
            'data' => $data,
            'total' => [
                'total_voltage' => $totalVoltage,
                'total_current' => $totalCurrent,
            ],
            'notifications' => $createdNotifications,
        ], 201);
    }

    private function notif(
        string $sensor,
        $value,
        string $unit,
        string $level,
        string $message,
        $timestamp = null,
        $lokasi = null
    ): array {
        return [
            'level' => $level,
            'status' => 'aktif',
            'kategori' => 'listrik',
            'sensor' => $sensor,
            'title' => ucfirst($level),
            'value' => $value,
            'unit' => $unit,
            'message' => $message,
            'lokasi' => $lokasi ?? 'Panel Listrik',
            'timestamp' => $timestamp,
        ];
    }

    private function getNotification(string $sensor, $value, $timestamp = null, $lokasi = null): ?array
    {
        if ($value === null) {
            return null;
        }

        $value = (float) $value;

        switch ($sensor) {
            case 'total_voltage':
                if ($value < 198) {
                    return $this->notif('total_voltage', $value, 'V', 'waspada', 'Tegangan berada di bawah batas normal.', $timestamp, $lokasi);
                }

                if ($value > 242) {
                    return $this->notif('total_voltage', $value, 'V', 'bahaya', 'Tegangan melebihi batas normal.', $timestamp, $lokasi);
                }
                break;

            case 'total_current':
                if ($value < 1) {
                    return $this->notif('total_current', $value, 'A', 'waspada', 'Arus berada di bawah batas normal.', $timestamp, $lokasi);
                }

                if ($value > 10) {
                    return $this->notif('total_current', $value, 'A', 'bahaya', 'Arus melebihi batas normal.', $timestamp, $lokasi);
                }
                break;

            case 'frecuency':
                if ($value < 49.5) {
                    return $this->notif('frecuency', $value, 'Hz', 'waspada', 'Frekuensi berada di bawah batas normal.', $timestamp, $lokasi);
                }

                if ($value > 50.5) {
                    return $this->notif('frecuency', $value, 'Hz', 'bahaya', 'Frekuensi melebihi batas normal.', $timestamp, $lokasi);
                }
                break;

            case 'power_factor':
                if ($value < 0.85) {
                    return $this->notif('power_factor', $value, '', 'waspada', 'Power factor berada di bawah batas normal.', $timestamp, $lokasi);
                }

                if ($value > 1) {
                    return $this->notif('power_factor', $value, '', 'bahaya', 'Power factor tidak normal.', $timestamp, $lokasi);
                }
                break;
        }

        return null;
    }

    private function getAllNotifications($data, $totalVoltage, $totalCurrent): array
    {
        $timestamp = $data?->time_stamp;
        $lokasi = 'Panel Listrik';

        return array_values(array_filter([
            $this->getNotification('total_voltage', $totalVoltage, $timestamp, $lokasi),
            $this->getNotification('total_current', $totalCurrent, $timestamp, $lokasi),
            $this->getNotification('frecuency', $data?->frecuency, $timestamp, $lokasi),
            $this->getNotification('power_factor', $data?->power_factor, $timestamp, $lokasi),
        ]));
    }

    public function getTotalVoltage(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        $voltage = collect([
            $data?->voltage_l1n,
            $data?->voltage_l2n,
            $data?->voltage_l3n,
        ])->filter(fn ($value) => $value !== null)->avg();

        return response()->json([
            'sensor' => 'total_voltage',
            'unit' => 'V',
            'value' => round((float) $voltage, 2),
            'timestamp' => $data?->time_stamp,
            'notification' => $this->getNotification('total_voltage', round((float) $voltage, 2), $data?->time_stamp, 'Panel Listrik'),
        ]);
    }

    public function getTotalCurrent(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        $current = collect([
            $data?->current_l1,
            $data?->current_l2,
            $data?->current_l3,
        ])->filter(fn ($value) => $value !== null)->sum();

        return response()->json([
            'sensor' => 'total_current',
            'unit' => 'A',
            'value' => round((float) $current, 2),
            'timestamp' => $data?->time_stamp,
            'notification' => $this->getNotification('total_current', round((float) $current, 2), $data?->time_stamp, 'Panel Listrik'),
        ]);
    }

    public function getVoltageL1L2(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'voltage_l1l2',
            'unit' => 'V',
            'value' => $data?->voltage_l1l2,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getVoltageL2L3(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'voltage_l2l3',
            'unit' => 'V',
            'value' => $data?->voltage_l2l3,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getVoltageL3L1(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'voltage_l3l1',
            'unit' => 'V',
            'value' => $data?->voltage_l3l1,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getVoltageL1N(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'voltage_l1n',
            'unit' => 'V',
            'value' => $data?->voltage_l1n,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getVoltageL2N(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'voltage_l2n',
            'unit' => 'V',
            'value' => $data?->voltage_l2n,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getVoltageL3N(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'voltage_l3n',
            'unit' => 'V',
            'value' => $data?->voltage_l3n,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getCurrentL1(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'current_l1',
            'unit' => 'A',
            'value' => $data?->current_l1,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getCurrentL2(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'current_l2',
            'unit' => 'A',
            'value' => $data?->current_l2,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getCurrentL3(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'current_l3',
            'unit' => 'A',
            'value' => $data?->current_l3,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getCurrentN(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'current_n',
            'unit' => 'A',
            'value' => $data?->current_n,
            'timestamp' => $data?->time_stamp,
        ]);
    }

    public function getFrecuency(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'frecuency',
            'unit' => 'Hz',
            'value' => $data?->frecuency,
            'timestamp' => $data?->time_stamp,
            'notification' => $this->getNotification('frecuency', $data?->frecuency, $data?->time_stamp, 'Panel Listrik'),
        ]);
    }

    public function getPowerFactor(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        return response()->json([
            'sensor' => 'power_factor',
            'unit' => '',
            'value' => $data?->power_factor,
            'timestamp' => $data?->time_stamp,
            'notification' => $this->getNotification('power_factor', $data?->power_factor, $data?->time_stamp, 'Panel Listrik'),
        ]);
    }

    public function getAll(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();

        $totalVoltage = collect([
            $data?->voltage_l1n,
            $data?->voltage_l2n,
            $data?->voltage_l3n,
        ])->filter(fn ($value) => $value !== null)->avg();

        $totalCurrent = collect([
            $data?->current_l1,
            $data?->current_l2,
            $data?->current_l3,
        ])->filter(fn ($value) => $value !== null)->sum();

        return response()->json([
            'type' => 'listrik',
            'timestamp' => $data?->time_stamp,
            'data' => $data,
            'total' => [
                'total_voltage' => round((float) $totalVoltage, 2),
                'total_current' => round((float) $totalCurrent, 2),
            ],
            'notifications' => $this->getAllNotifications($data, round((float) $totalVoltage, 2), round((float) $totalCurrent, 2)),
        ]);
    }

    public function getHistory(): JsonResponse
    {
        $data = RsptdDataBaru::orderBy('time_stamp', 'desc')->limit(100)->get();

        return response()->json([
            'type' => 'listrik_history',
            'total' => $data->count(),
            'data' => $data,
        ]);
    }

    private function formatTimestamp($timestamp): ?string
    {
        if (!$timestamp) {
            return null;
        }

        return Carbon::parse($timestamp)->format('Y-m-d H:i:s');
    }
}