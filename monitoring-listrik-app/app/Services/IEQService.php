<?php

namespace App\Services;

class IEQService
{
    public static function calculate($data): array
    {
        $scores = [];

        $scores[] = self::scoreSuhu($data->suhu ?? null);
        $scores[] = self::scoreKelembapan($data->kelembapan ?? null);
        $scores[] = self::scorePm25($data->pm25 ?? null);
        $scores[] = self::scorePm10($data->pm10 ?? null);
        $scores[] = self::scoreCo($data->gas_co ?? null);
        $scores[] = self::scoreCo2($data->gas_co2 ?? null);
        $scores[] = self::scoreCahaya($data->cahaya ?? null);
        $scores[] = self::scoreKebisingan($data->kebisingan ?? null);

        $scores = array_filter($scores, fn ($s) => $s !== null);

        if (count($scores) === 0) {
            return [
                'score' => null,
                'status' => 'TIDAK ADA DATA',
            ];
        }

        $avg = array_sum($scores) / count($scores);

        return [
            'score' => round($avg),
            'status' => self::getStatus($avg),
        ];
    }

    private static function scoreSuhu($v): ?int
    {
        if ($v === null) return null;
        if ($v < 20) return 70;
        if ($v > 29) return 40;
        return 100;
    }

    private static function scoreKelembapan($v): ?int
    {
        if ($v === null) return null;
        if ($v < 40) return 70;
        if ($v > 60) return 40;
        return 100;
    }

    private static function scorePm25($v): ?int
    {
        if ($v === null) return null;
        return $v > 35 ? 40 : 100;
    }

    private static function scorePm10($v): ?int
    {
        if ($v === null) return null;
        return $v > 70 ? 40 : 100;
    }

    private static function scoreCo($v): ?int
    {
        if ($v === null) return null;
        return $v > 10000 ? 40 : 100;
    }

    private static function scoreCo2($v): ?int
    {
        if ($v === null) return null;
        return $v > 1 ? 40 : 100;
    }

    private static function scoreCahaya($v): ?int
    {
        if ($v === null) return null;
        return $v < 100 ? 70 : 100;
    }

    private static function scoreKebisingan($v): ?int
    {
        if ($v === null) return null;
        return $v > 45 ? 40 : 100;
    }

    private static function getStatus($score): string
    {
        if ($score >= 80) return 'BAIK';
        if ($score >= 60) return 'KURANG BAIK';
        return 'BURUK';
    }
}