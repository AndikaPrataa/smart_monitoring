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
        $records = $records->values();

        if ($records->count() < 2) {
            return 0;
        }

        $totalKwh = 0;

        for ($i = 1; $i < $records->count(); $i++) {
            $previous = $records[$i - 1];
            $current = $records[$i];

            $previousTime = strtotime($previous->time_stamp);
            $currentTime = strtotime($current->time_stamp);

            $diffSeconds = $currentTime - $previousTime;

            if ($diffSeconds <= 0) {
                continue;
            }

            $previousPowerWatt = self::totalActivePowerWatt($previous);
            $currentPowerWatt = self::totalActivePowerWatt($current);

            $averagePowerWatt = ($previousPowerWatt + $currentPowerWatt) / 2;

            $energyKwh = ($averagePowerWatt / 1000) * ($diffSeconds / 3600);

            $totalKwh += $energyKwh;
        }

        return $totalKwh;
    }

    public static function calculateCost(float $energyKwh, int $tarifPerKwh = self::TARIF_PER_KWH): float
    {
        return $energyKwh * $tarifPerKwh;
    }

    public static function calculateRealtimeFromLastTwoRecords($records): array
    {
        $records = $records->values();

        if ($records->count() < 2) {
            return [
                'energy_kwh' => 0,
                'tarif_per_kwh' => self::TARIF_PER_KWH,
                'biaya' => 0,
                'keterangan' => 'Data belum cukup. Minimal membutuhkan 2 data untuk menghitung energi realtime.',
            ];
        }

        $energyKwh = self::calculateKwhFromRecords($records);
        $biaya = self::calculateCost($energyKwh);

        return [
            'energy_kwh' => round($energyKwh, 6),
            'tarif_per_kwh' => self::TARIF_PER_KWH,
            'biaya' => round($biaya, 2),
            'keterangan' => 'Energi dihitung dari dua data terakhir berdasarkan selisih timestamp.',
        ];
    }
}