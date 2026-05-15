<?php

namespace App\Services;

class EnergyService
{
    public const TARIF_PER_KWH = 1260;

    public static function totalActivePowerWatt($record): float
    {
        return (float) ($record->active_power_r ?? 0)
            + (float) ($record->active_power_s ?? 0)
            + (float) ($record->active_power_t ?? 0);
    }

    public static function calculateKwhFromRecords($records): float
    {
        // Logic baru:
        // Tidak lagi menghitung berdasarkan selisih waktu/timestamp.
        // Total watt R + S + T dari semua data langsung dikonversi menjadi kW.
        $totalWatt = $records->sum(function ($record) {
            return self::totalActivePowerWatt($record);
        });

        return $totalWatt / 1000;
    }

    public static function calculateCost(float $energyKwh, int $tarifPerKwh = self::TARIF_PER_KWH): float
    {
        return $energyKwh * $tarifPerKwh;
    }

    public static function calculateRealtimeFromLastTwoRecords($records): array
    {
        $records = $records->values();

        if ($records->count() < 1) {
            return [
                'energy_kwh' => 0,
                'tarif_per_kwh' => self::TARIF_PER_KWH,
                'biaya' => 0,
                'keterangan' => 'Data belum tersedia untuk menghitung daya realtime.',
            ];
        }

        // Logic realtime baru:
        // Ambil data terbaru saja, lalu total watt RST dikonversi ke kW.
        $latestRecord = $records->last();
        $energyKwh = self::totalActivePowerWatt($latestRecord) / 1000;
        $biaya = self::calculateCost($energyKwh);

        return [
            'energy_kwh' => round($energyKwh, 6),
            'tarif_per_kwh' => self::TARIF_PER_KWH,
            'biaya' => round($biaya, 2),
            'keterangan' => 'Total watt RST data terbaru dikonversi langsung menjadi kW.',
        ];
    }
}