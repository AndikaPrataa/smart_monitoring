<?php

namespace App\Services;

class IEQService
{
    public static function calculate($data): array
    {
        $scores = [];

        $scores['suhu'] = self::scoreSuhu($data?->suhu);
        $scores['kelembapan'] = self::scoreKelembapan($data?->kelembapan);
        $scores['pm25'] = self::scorePm25($data?->pm25);
        $scores['pm10'] = self::scorePm10($data?->pm10);
        $scores['gas_co'] = self::scoreGasCo($data?->gas_co);
        $scores['gas_co2'] = self::scoreGasCo2($data?->gas_co2);
        $scores['tvoc'] = self::scoreTvoc($data?->tvoc);
        $scores['cahaya'] = self::scoreCahaya($data?->cahaya);
        $scores['kebisingan'] = self::scoreKebisingan($data?->kebisingan);

        $validScores = array_filter($scores, function ($score) {
            return $score !== null;
        });

        if (count($validScores) === 0) {
            return [
                'score' => 0,
                'status' => 'TIDAK ADA DATA',
                'description' => 'Belum ada data sensor yang dapat dihitung',
                'details' => $scores,
            ];
        }

        $average = array_sum($validScores) / count($validScores);

        return [
            'score' => round($average, 2),
            'status' => self::getStatus($average),
            'description' => self::getDescription($average),
            'details' => $scores,
        ];
    }

    private static function scoreSuhu($value): ?int
    {
        if ($value === null) {
            return null;
        }

        if ($value < 20) {
            return 60;
        }

        if ($value > 29) {
            return 40;
        }

        return 100;
    }

    private static function scoreKelembapan($value): ?int
    {
        if ($value === null) {
            return null;
        }

        if ($value < 40) {
            return 60;
        }

        if ($value > 60) {
            return 40;
        }

        return 100;
    }

    private static function scorePm25($value): ?int
    {
        if ($value === null) {
            return null;
        }

        return $value > 35 ? 40 : 100;
    }

    private static function scorePm10($value): ?int
    {
        if ($value === null) {
            return null;
        }

        return $value > 70 ? 40 : 100;
    }

    private static function scoreGasCo($value): ?int
    {
        if ($value === null) {
            return null;
        }

        return $value > 10000 ? 40 : 100;
    }

    private static function scoreGasCo2($value): ?int
    {
        if ($value === null) {
            return null;
        }

        return $value > 1000 ? 40 : 100;
    }

    private static function scoreTvoc($value): ?int
    {
        if ($value === null) {
            return null;
        }

        return $value > 3 ? 40 : 100;
    }

    private static function scoreCahaya($value): ?int
    {
        if ($value === null) {
            return null;
        }

        return $value < 100 ? 60 : 100;
    }

    private static function scoreKebisingan($value): ?int
    {
        if ($value === null) {
            return null;
        }

        return $value > 45 ? 40 : 100;
    }

    private static function getStatus(float $score): string
    {
        if ($score >= 80) {
            return 'BAIK';
        }

        if ($score >= 60) {
            return 'CUKUP';
        }

        return 'KURANG BAIK';
    }

    private static function getDescription(float $score): string
    {
        if ($score >= 80) {
            return 'Kondisi kualitas lingkungan dalam keadaan baik';
        }

        if ($score >= 60) {
            return 'Kondisi kualitas lingkungan cukup, namun terdapat beberapa parameter yang perlu diperhatikan';
        }

        return 'Kondisi kualitas lingkungan kurang baik dan perlu dilakukan tindakan pemeriksaan';
    }
}