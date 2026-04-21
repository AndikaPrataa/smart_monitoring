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
use Illuminate\Http\JsonResponse;

class DayaController extends Controller
{
    public function getActivePowerR(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ActivePowerRUpdated($data->active_power_r));
        return response()->json(['sensor' => 'active_power_r', 'unit' => 'W', 'value' => $data?->active_power_r, 'timestamp' => $data?->time_stamp]);
    }

    public function getActivePowerS(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ActivePowerSUpdated($data->active_power_s));
        return response()->json(['sensor' => 'active_power_s', 'unit' => 'W', 'value' => $data?->active_power_s, 'timestamp' => $data?->time_stamp]);
    }

    public function getActivePowerT(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ActivePowerTUpdated($data->active_power_t));
        return response()->json(['sensor' => 'active_power_t', 'unit' => 'W', 'value' => $data?->active_power_t, 'timestamp' => $data?->time_stamp]);
    }

    public function getReactivePowerR(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ReactivePowerRUpdated($data->reactive_power_r));
        return response()->json(['sensor' => 'reactive_power_r', 'unit' => 'VAR', 'value' => $data?->reactive_power_r, 'timestamp' => $data?->time_stamp]);
    }

    public function getReactivePowerS(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ReactivePowerSUpdated($data->reactive_power_s));
        return response()->json(['sensor' => 'reactive_power_s', 'unit' => 'VAR', 'value' => $data?->reactive_power_s, 'timestamp' => $data?->time_stamp]);
    }

    public function getReactivePowerT(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ReactivePowerTUpdated($data->reactive_power_t));
        return response()->json(['sensor' => 'reactive_power_t', 'unit' => 'VAR', 'value' => $data?->reactive_power_t, 'timestamp' => $data?->time_stamp]);
    }

    public function getApparentPowerR(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ApparentPowerRUpdated($data->apparent_power_r));
        return response()->json(['sensor' => 'apparent_power_r', 'unit' => 'VA', 'value' => $data?->apparent_power_r, 'timestamp' => $data?->time_stamp]);
    }

    public function getApparentPowerS(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ApparentPowerSUpdated($data->apparent_power_s));
        return response()->json(['sensor' => 'apparent_power_s', 'unit' => 'VA', 'value' => $data?->apparent_power_s, 'timestamp' => $data?->time_stamp]);
    }

    public function getApparentPowerT(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) event(new ApparentPowerTUpdated($data->apparent_power_t));
        return response()->json(['sensor' => 'apparent_power_t', 'unit' => 'VA', 'value' => $data?->apparent_power_t, 'timestamp' => $data?->time_stamp]);
    }

    public function getAll(): JsonResponse
    {
        $data = RsptdPowerBaru::latest('time_stamp')->first();
        if ($data) {
            event(new ActivePowerRUpdated($data->active_power_r));
            event(new ActivePowerSUpdated($data->active_power_s));
            event(new ActivePowerTUpdated($data->active_power_t));
            event(new ReactivePowerRUpdated($data->reactive_power_r));
            event(new ReactivePowerSUpdated($data->reactive_power_s));
            event(new ReactivePowerTUpdated($data->reactive_power_t));
            event(new ApparentPowerRUpdated($data->apparent_power_r));
            event(new ApparentPowerSUpdated($data->apparent_power_s));
            event(new ApparentPowerTUpdated($data->apparent_power_t));
        }
        return response()->json(['type' => 'daya', 'timestamp' => $data?->time_stamp, 'data' => $data]);
    }

    public function getHistory(): JsonResponse
    {
        $data = RsptdPowerBaru::orderBy('time_stamp', 'desc')->limit(100)->get();
        return response()->json(['type' => 'daya_history', 'total' => count($data), 'data' => $data]);
    }
}
