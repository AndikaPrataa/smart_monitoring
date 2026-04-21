<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\RsptdDataBaru;
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
use Illuminate\Http\JsonResponse;

class ListrikController extends Controller
{
    public function getVoltageL1L2(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new VoltageL1L2Updated($data->voltage_l1l2));
        return response()->json(['sensor' => 'voltage_l1l2', 'unit' => 'V', 'value' => $data?->voltage_l1l2, 'timestamp' => $data?->time_stamp]);
    }

    public function getVoltageL2L3(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new VoltageL2L3Updated($data->voltage_l2l3));
        return response()->json(['sensor' => 'voltage_l2l3', 'unit' => 'V', 'value' => $data?->voltage_l2l3, 'timestamp' => $data?->time_stamp]);
    }

    public function getVoltageL3L1(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new VoltageL3L1Updated($data->voltage_l3l1));
        return response()->json(['sensor' => 'voltage_l3l1', 'unit' => 'V', 'value' => $data?->voltage_l3l1, 'timestamp' => $data?->time_stamp]);
    }

    public function getVoltageL1N(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new VoltageL1NUpdated($data->voltage_l1n));
        return response()->json(['sensor' => 'voltage_l1n', 'unit' => 'V', 'value' => $data?->voltage_l1n, 'timestamp' => $data?->time_stamp]);
    }

    public function getVoltageL2N(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new VoltageL2NUpdated($data->voltage_l2n));
        return response()->json(['sensor' => 'voltage_l2n', 'unit' => 'V', 'value' => $data?->voltage_l2n, 'timestamp' => $data?->time_stamp]);
    }

    public function getVoltageL3N(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new VoltageL3NUpdated($data->voltage_l3n));
        return response()->json(['sensor' => 'voltage_l3n', 'unit' => 'V', 'value' => $data?->voltage_l3n, 'timestamp' => $data?->time_stamp]);
    }

    public function getCurrentL1(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new CurrentL1Updated($data->current_l1));
        return response()->json(['sensor' => 'current_l1', 'unit' => 'A', 'value' => $data?->current_l1, 'timestamp' => $data?->time_stamp]);
    }

    public function getCurrentL2(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new CurrentL2Updated($data->current_l2));
        return response()->json(['sensor' => 'current_l2', 'unit' => 'A', 'value' => $data?->current_l2, 'timestamp' => $data?->time_stamp]);
    }

    public function getCurrentL3(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new CurrentL3Updated($data->current_l3));
        return response()->json(['sensor' => 'current_l3', 'unit' => 'A', 'value' => $data?->current_l3, 'timestamp' => $data?->time_stamp]);
    }

    public function getCurrentN(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new CurrentNUpdated($data->current_n));
        return response()->json(['sensor' => 'current_n', 'unit' => 'A', 'value' => $data?->current_n, 'timestamp' => $data?->time_stamp]);
    }

    public function getFrecuency(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new FrecuencyUpdated($data->frecuency));
        return response()->json(['sensor' => 'frecuency', 'unit' => 'Hz', 'value' => $data?->frecuency, 'timestamp' => $data?->time_stamp]);
    }

    public function getPowerFactor(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) event(new PowerFactorUpdated($data->power_factor));
        return response()->json(['sensor' => 'power_factor', 'unit' => '', 'value' => $data?->power_factor, 'timestamp' => $data?->time_stamp]);
    }

    public function getAll(): JsonResponse
    {
        $data = RsptdDataBaru::latest('time_stamp')->first();
        if ($data) {
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
        }
        return response()->json(['type' => 'listrik', 'timestamp' => $data?->time_stamp, 'data' => $data]);
    }

    public function getHistory(): JsonResponse
    {
        $data = RsptdDataBaru::orderBy('time_stamp', 'desc')->limit(100)->get();
        return response()->json(['type' => 'listrik_history', 'total' => count($data), 'data' => $data]);
    }
}
