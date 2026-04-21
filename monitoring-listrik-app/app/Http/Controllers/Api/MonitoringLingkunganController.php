<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\MonitoringLingkungan;
use App\Events\SuhuUpdated;
use App\Events\KelembapanUpdated;
use App\Events\Pm25Updated;
use App\Events\Pm10Updated;
use App\Events\Eco2Updated;
use App\Events\TvocUpdated;
use App\Events\LokasiUpdated;
use Illuminate\Http\JsonResponse;

class MonitoringLingkunganController extends Controller
{
    public function getSuhu(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) event(new SuhuUpdated($data->suhu));
        return response()->json([
            'sensor' => 'suhu', 'unit' => '°C', 'value' => $data?->suhu,
            'timestamp' => $data?->waktu, 'detail' => 'Temperature (Suhu) sensor'
        ]);
    }

    public function getKelembapan(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) event(new KelembapanUpdated($data->kelembapan));
        return response()->json([
            'sensor' => 'kelembapan', 'unit' => '%', 'value' => $data?->kelembapan,
            'timestamp' => $data?->waktu, 'detail' => 'Humidity (Kelembapan) sensor'
        ]);
    }

    public function getPm25(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) event(new Pm25Updated($data->pm25));
        return response()->json([
            'sensor' => 'pm25', 'unit' => 'µg/m³', 'value' => $data?->pm25,
            'timestamp' => $data?->waktu, 'detail' => 'PM2.5 (Fine Particulate Matter)'
        ]);
    }

    public function getPm10(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) event(new Pm10Updated($data->pm10));
        return response()->json([
            'sensor' => 'pm10', 'unit' => 'µg/m³', 'value' => $data?->pm10,
            'timestamp' => $data?->waktu, 'detail' => 'PM10 (Coarse Particulate Matter)'
        ]);
    }

    public function getEco2(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) event(new Eco2Updated($data->eco2));
        return response()->json([
            'sensor' => 'eco2', 'unit' => 'ppm', 'value' => $data?->eco2,
            'timestamp' => $data?->waktu, 'detail' => 'Equivalent CO2'
        ]);
    }

    public function getTvoc(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) event(new TvocUpdated($data->tvoc));
        return response()->json([
            'sensor' => 'tvoc', 'unit' => 'ppb', 'value' => $data?->tvoc,
            'timestamp' => $data?->waktu, 'detail' => 'Total Volatile Organic Compounds'
        ]);
    }

    public function getLokasi(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) event(new LokasiUpdated($data->lokasi));
        return response()->json([
            'sensor' => 'lokasi', 'unit' => 'text', 'value' => $data?->lokasi,
            'timestamp' => $data?->waktu, 'detail' => 'Monitoring Location'
        ]);
    }

    public function getAll(): JsonResponse
    {
        $data = MonitoringLingkungan::latest('waktu')->first();
        if ($data) {
            event(new SuhuUpdated($data->suhu));
            event(new KelembapanUpdated($data->kelembapan));
            event(new Pm25Updated($data->pm25));
            event(new Pm10Updated($data->pm10));
            event(new Eco2Updated($data->eco2));
            event(new TvocUpdated($data->tvoc));
            event(new LokasiUpdated($data->lokasi));
        }
        return response()->json([
            'type' => 'lingkungan', 'timestamp' => $data?->waktu,
            'data' => $data
        ]);
    }

    public function getHistory(): JsonResponse
    {
        $data = MonitoringLingkungan::orderBy('waktu', 'desc')->limit(100)->get();
        return response()->json(['type' => 'lingkungan_history', 'total' => count($data), 'data' => $data]);
    }
}
