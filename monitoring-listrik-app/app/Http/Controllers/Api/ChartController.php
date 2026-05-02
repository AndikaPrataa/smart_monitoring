<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Carbon\Carbon;
use Carbon\CarbonPeriod;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ChartController extends Controller
{
    public function monitoring(Request $request): JsonResponse
    {
        return $this->buildChart($request, 'monitoring');
    }

    public function listrik(Request $request): JsonResponse
    {
        return $this->buildChart($request, 'listrik');
    }

    public function daya(Request $request): JsonResponse
    {
        return $this->buildChart($request, 'daya');
    }

    private function buildChart(Request $request, string $type): JsonResponse
    {
        $config = $this->getConfig($type);

        $filter = $request->query('filter', 'jam');

        if (!in_array($filter, ['detik', 'jam', 'hari', 'bulan', 'tahun'], true)) {
            return response()->json([
                'success' => false,
                'message' => 'Filter tidak valid',
                'allowed_filters' => ['detik', 'jam', 'hari', 'bulan', 'tahun'],
            ], 422);
        }

        $sensors = $this->parseSensors($request, $config);

        if (empty($sensors)) {
            return response()->json([
                'success' => false,
                'message' => 'Parameter sensor atau sensors wajib diisi',
                'contoh' => [
                    'single' => '?sensor=suhu&filter=jam',
                    'multiple' => '?sensors=suhu,kelembapan&filter=jam',
                ],
                'allowed_sensors' => array_keys($config['sensors']),
            ], 422);
        }

        $invalidSensors = array_values(array_filter($sensors, function ($sensor) use ($config) {
            return !array_key_exists($sensor, $config['sensors']);
        }));

        if (!empty($invalidSensors)) {
            return response()->json([
                'success' => false,
                'message' => 'Ada sensor yang tidak valid untuk chart ' . $type,
                'invalid_sensors' => $invalidSensors,
                'allowed_sensors' => array_keys($config['sensors']),
            ], 422);
        }

        if (in_array($filter, ['detik', 'jam'], true)) {
            $series = $this->getRawSeries($request, $config, $sensors, $filter);
            $aggregation = 'raw';
        } else {
            $series = $this->getAggregatedSeries($request, $config, $sensors, $filter, $type);
            $aggregation = 'mixed';
        }

        return response()->json([
            'success' => true,
            'message' => 'Data chart berhasil diambil',
            'type' => $type,
            'filter' => $filter,
            'aggregation' => $aggregation,
            'x_axis' => $this->getXAxisLabel($filter),
            'series' => $series,
            'meta' => [
                'tanggal_mulai' => $request->query('tanggal_mulai'),
                'tanggal_selesai' => $request->query('tanggal_selesai'),
                'jam_mulai' => $request->query('jam_mulai'),
                'jam_selesai' => $request->query('jam_selesai'),
                'bulan' => $request->query('bulan', now()->month),
                'tahun' => $request->query('tahun', now()->year),
            ],
        ]);
    }

    private function parseSensors(Request $request, array $config): array
    {
        $sensor = $request->query('sensor');
        $sensors = $request->query('sensors');

        if ($sensors) {
            return array_values(array_filter(array_map('trim', explode(',', $sensors))));
        }

        if ($sensor) {
            return [trim($sensor)];
        }

        return [];
    }

    private function getRawSeries(Request $request, array $config, array $sensors, string $filter): array
    {
        $result = [];

        foreach ($sensors as $sensor) {
            $sensorConfig = $config['sensors'][$sensor];

            $query = DB::table($config['table']);

            $this->applyRawTimeFilter($query, $request, $config['time_column'], $filter);

            $expression = $sensorConfig['expression'];

            $query->whereRaw("($expression) IS NOT NULL");

            $limit = (int) $request->query('limit', $filter === 'detik' ? 300 : 5000);

            if ($limit <= 0) {
                $limit = $filter === 'detik' ? 300 : 5000;
            }

            if ($limit > 20000) {
                $limit = 20000;
            }

            $rows = $query
                ->selectRaw($config['time_column'] . " as timestamp, ($expression) as value")
                ->orderBy($config['time_column'], 'desc')
                ->limit($limit)
                ->get()
                ->sortBy('timestamp')
                ->values();

            $result[] = [
                'sensor' => $sensor,
                'label' => $sensorConfig['label'],
                'unit' => $sensorConfig['unit'],
                'aggregation' => 'raw',
                'data' => $rows->map(function ($row) use ($filter) {
                    return [
                        'label' => Carbon::parse($row->timestamp)->format($filter === 'detik' ? 'H:i:s' : 'H:i'),
                        'timestamp' => $row->timestamp,
                        'value' => $row->value !== null ? round((float) $row->value, 4) : null,
                    ];
                })->values(),
            ];
        }

        return $result;
    }

    private function getAggregatedSeries(Request $request, array $config, array $sensors, string $filter, string $type): array
    {
        $result = [];

        foreach ($sensors as $sensor) {
            $sensorConfig = $config['sensors'][$sensor];

            if ($filter === 'hari') {
                $data = $this->getDailyAggregation($request, $config, $sensor, $sensorConfig, $type);
            } else {
                $data = $this->getMonthlyAggregation($request, $config, $sensor, $sensorConfig, $type);
            }

            $result[] = [
                'sensor' => $sensor,
                'label' => $sensorConfig['label'],
                'unit' => $sensorConfig['unit'],
                'aggregation' => $this->getAggregationType($type, $sensor),
                'data' => $data,
            ];
        }

        return $result;
    }

    private function getDailyAggregation(Request $request, array $config, string $sensor, array $sensorConfig, string $type): array
    {
        $timeColumn = $config['time_column'];
        $expression = $sensorConfig['expression'];
        $aggregation = $this->getAggregationSql($type, $sensor, $expression);

        $tanggalMulai = $request->query('tanggal_mulai');
        $tanggalSelesai = $request->query('tanggal_selesai');

        $query = DB::table($config['table']);

        if ($tanggalMulai && $tanggalSelesai) {
            $startDate = Carbon::parse($tanggalMulai)->startOfDay();
            $endDate = Carbon::parse($tanggalSelesai)->endOfDay();

            $this->applyCustomDateTimeFilter($query, $request, $timeColumn);

            $rows = $query
                ->selectRaw("DATE($timeColumn) as group_key, $aggregation as value")
                ->whereRaw("($expression) IS NOT NULL")
                ->groupByRaw("DATE($timeColumn)")
                ->orderByRaw("DATE($timeColumn) ASC")
                ->get()
                ->keyBy('group_key');

            $period = CarbonPeriod::create($startDate, $endDate);

            $result = [];

            foreach ($period as $date) {
                $key = $date->format('Y-m-d');
                $row = $rows->get($key);

                $result[] = [
                    'label' => $date->format('d M'),
                    'date' => $key,
                    'value' => $row ? round((float) $row->value, 4) : null,
                ];
            }

            return $result;
        }

        $month = (int) $request->query('bulan', now()->month);
        $year = (int) $request->query('tahun', now()->year);

        $daysInMonth = Carbon::create($year, $month, 1)->daysInMonth;

        $query->whereYear($timeColumn, $year)
            ->whereMonth($timeColumn, $month);

        $this->applyTimeOnlyFilter($query, $request, $timeColumn);

        $rows = $query
            ->selectRaw("DAY($timeColumn) as group_key, $aggregation as value")
            ->whereRaw("($expression) IS NOT NULL")
            ->groupByRaw("DAY($timeColumn)")
            ->orderByRaw("DAY($timeColumn) ASC")
            ->get()
            ->keyBy('group_key');

        $result = [];

        for ($day = 1; $day <= $daysInMonth; $day++) {
            $row = $rows->get($day);

            $result[] = [
                'label' => (string) $day,
                'date' => Carbon::create($year, $month, $day)->format('Y-m-d'),
                'value' => $row ? round((float) $row->value, 4) : null,
            ];
        }

        return $result;
    }

    private function getMonthlyAggregation(Request $request, array $config, string $sensor, array $sensorConfig, string $type): array
    {
        $timeColumn = $config['time_column'];
        $expression = $sensorConfig['expression'];
        $aggregation = $this->getAggregationSql($type, $sensor, $expression);

        $year = (int) $request->query('tahun', now()->year);

        $query = DB::table($config['table'])
            ->whereYear($timeColumn, $year);

        $this->applyTimeOnlyFilter($query, $request, $timeColumn);

        $rows = $query
            ->selectRaw("MONTH($timeColumn) as group_key, $aggregation as value")
            ->whereRaw("($expression) IS NOT NULL")
            ->groupByRaw("MONTH($timeColumn)")
            ->orderByRaw("MONTH($timeColumn) ASC")
            ->get()
            ->keyBy('group_key');

        $result = [];

        for ($month = 1; $month <= 12; $month++) {
            $row = $rows->get($month);

            $result[] = [
                'label' => Carbon::create($year, $month, 1)->locale('id')->translatedFormat('M'),
                'month' => $month,
                'year' => $year,
                'value' => $row ? round((float) $row->value, 4) : null,
            ];
        }

        return $result;
    }

    private function applyRawTimeFilter($query, Request $request, string $timeColumn, string $filter): void
    {
        $tanggalMulai = $request->query('tanggal_mulai');
        $tanggalSelesai = $request->query('tanggal_selesai');

        if ($tanggalMulai && $tanggalSelesai) {
            $this->applyCustomDateTimeFilter($query, $request, $timeColumn);
            return;
        }

        if ($filter === 'detik') {
            $query->where($timeColumn, '>=', now()->subMinutes(10));
        } elseif ($filter === 'jam') {
            $query->where($timeColumn, '>=', now()->subHours(12));
        }

        $this->applyTimeOnlyFilter($query, $request, $timeColumn);
    }

    private function applyCustomDateTimeFilter($query, Request $request, string $timeColumn): void
    {
        $tanggalMulai = $request->query('tanggal_mulai');
        $tanggalSelesai = $request->query('tanggal_selesai');
        $jamMulai = $request->query('jam_mulai');
        $jamSelesai = $request->query('jam_selesai');

        $start = Carbon::parse($tanggalMulai . ' ' . ($jamMulai ?: '00:00:00'));
        $end = Carbon::parse($tanggalSelesai . ' ' . ($jamSelesai ?: '23:59:59'));

        $query->whereBetween($timeColumn, [$start, $end]);
    }

    private function applyTimeOnlyFilter($query, Request $request, string $timeColumn): void
    {
        $jamMulai = $request->query('jam_mulai');
        $jamSelesai = $request->query('jam_selesai');

        if ($jamMulai && $jamSelesai) {
            $query->whereTime($timeColumn, '>=', $jamMulai)
                ->whereTime($timeColumn, '<=', $jamSelesai);
        }
    }

    private function getAggregationType(string $type, string $sensor): string
    {
        if ($type === 'daya' && $this->isActivePowerSensor($sensor)) {
            return 'sum';
        }

        return 'avg';
    }

    private function getAggregationSql(string $type, string $sensor, string $expression): string
    {
        if ($type === 'daya' && $this->isActivePowerSensor($sensor)) {
            return "SUM($expression)";
        }

        return "AVG($expression)";
    }

    private function isActivePowerSensor(string $sensor): bool
    {
        return in_array($sensor, [
            'active_power_r',
            'active_power_s',
            'active_power_t',
            'total_active_power',
        ], true);
    }

    private function getXAxisLabel(string $filter): string
    {
        return match ($filter) {
            'detik' => 'Detik',
            'jam' => 'Jam',
            'hari' => 'Tanggal',
            'bulan' => 'Bulan',
            'tahun' => 'Bulan',
            default => 'Waktu',
        };
    }

    private function getConfig(string $type): array
    {
        return match ($type) {
            'monitoring' => [
                'table' => 'monitoring_lingkungan',
                'time_column' => 'waktu',
                'sensors' => [
                    'suhu' => [
                        'label' => 'Suhu',
                        'expression' => 'suhu',
                        'unit' => '°C',
                    ],
                    'kelembapan' => [
                        'label' => 'Kelembapan',
                        'expression' => 'kelembapan',
                        'unit' => '%',
                    ],
                    'pm25' => [
                        'label' => 'PM2.5',
                        'expression' => 'pm25',
                        'unit' => 'µg/m³',
                    ],
                    'pm10' => [
                        'label' => 'PM10',
                        'expression' => 'pm10',
                        'unit' => 'µg/m³',
                    ],
                    'gas_co' => [
                        'label' => 'CO',
                        'expression' => 'gas_co',
                        'unit' => 'ppm',
                    ],
                    'gas_co2' => [
                        'label' => 'eCO2',
                        'expression' => 'gas_co2',
                        'unit' => 'ppm',
                    ],
                    'tvoc' => [
                        'label' => 'VOC',
                        'expression' => 'tvoc',
                        'unit' => 'ppm',
                    ],
                    'cahaya' => [
                        'label' => 'Cahaya',
                        'expression' => 'cahaya',
                        'unit' => 'lux',
                    ],
                    'kebisingan' => [
                        'label' => 'Kebisingan',
                        'expression' => 'kebisingan',
                        'unit' => 'dB',
                    ],
                ],
            ],

            'listrik' => [
                'table' => 'rsptn_data_baru',
                'time_column' => 'time_stamp',
                'sensors' => [
                    'voltage_l1l2' => [
                        'label' => 'L1-L2',
                        'expression' => 'voltage_l1l2',
                        'unit' => 'V',
                    ],
                    'voltage_l2l3' => [
                        'label' => 'L2-L3',
                        'expression' => 'voltage_l2l3',
                        'unit' => 'V',
                    ],
                    'voltage_l3l1' => [
                        'label' => 'L3-L1',
                        'expression' => 'voltage_l3l1',
                        'unit' => 'V',
                    ],
                    'voltage_l1n' => [
                        'label' => 'L1-N',
                        'expression' => 'voltage_l1n',
                        'unit' => 'V',
                    ],
                    'voltage_l2n' => [
                        'label' => 'L2-N',
                        'expression' => 'voltage_l2n',
                        'unit' => 'V',
                    ],
                    'voltage_l3n' => [
                        'label' => 'L3-N',
                        'expression' => 'voltage_l3n',
                        'unit' => 'V',
                    ],
                    'current_l1' => [
                        'label' => 'L1',
                        'expression' => 'current_l1',
                        'unit' => 'A',
                    ],
                    'current_l2' => [
                        'label' => 'L2',
                        'expression' => 'current_l2',
                        'unit' => 'A',
                    ],
                    'current_l3' => [
                        'label' => 'L3',
                        'expression' => 'current_l3',
                        'unit' => 'A',
                    ],
                    'current_n' => [
                        'label' => 'N',
                        'expression' => 'current_n',
                        'unit' => 'A',
                    ],
                    'frecuency' => [
                        'label' => 'Frekuensi',
                        'expression' => 'frecuency',
                        'unit' => 'Hz',
                    ],
                    'power_factor' => [
                        'label' => 'Faktor Daya',
                        'expression' => 'power_factor',
                        'unit' => 'factor',
                    ],
                    'total_voltage' => [
                        'label' => 'Total Tegangan',
                        'expression' => '(
                            COALESCE(voltage_l1n, 0) +
                            COALESCE(voltage_l2n, 0) +
                            COALESCE(voltage_l3n, 0)
                        ) / NULLIF(
                            (CASE WHEN voltage_l1n IS NULL THEN 0 ELSE 1 END) +
                            (CASE WHEN voltage_l2n IS NULL THEN 0 ELSE 1 END) +
                            (CASE WHEN voltage_l3n IS NULL THEN 0 ELSE 1 END),
                            0
                        )',
                        'unit' => 'V',
                    ],
                    'total_current' => [
                        'label' => 'Total Arus',
                        'expression' => '(
                            COALESCE(current_l1, 0) +
                            COALESCE(current_l2, 0) +
                            COALESCE(current_l3, 0)
                        )',
                        'unit' => 'A',
                    ],
                ],
            ],

            'daya' => [
                'table' => 'rsptn_power_baru',
                'time_column' => 'time_stamp',
                'sensors' => [
                    'active_power_r' => [
                        'label' => 'R',
                        'expression' => 'active_power_r',
                        'unit' => 'W',
                    ],
                    'active_power_s' => [
                        'label' => 'S',
                        'expression' => 'active_power_s',
                        'unit' => 'W',
                    ],
                    'active_power_t' => [
                        'label' => 'T',
                        'expression' => 'active_power_t',
                        'unit' => 'W',
                    ],
                    'total_active_power' => [
                        'label' => 'Total Daya Aktif',
                        'expression' => '(
                            COALESCE(active_power_r, 0) +
                            COALESCE(active_power_s, 0) +
                            COALESCE(active_power_t, 0)
                        )',
                        'unit' => 'W',
                    ],
                    'reactive_power_r' => [
                        'label' => 'Reactive R',
                        'expression' => 'reactive_power_r',
                        'unit' => 'VAR',
                    ],
                    'reactive_power_s' => [
                        'label' => 'Reactive S',
                        'expression' => 'reactive_power_s',
                        'unit' => 'VAR',
                    ],
                    'reactive_power_t' => [
                        'label' => 'Reactive T',
                        'expression' => 'reactive_power_t',
                        'unit' => 'VAR',
                    ],
                    'apparent_power_r' => [
                        'label' => 'Apparent R',
                        'expression' => 'apparent_power_r',
                        'unit' => 'VA',
                    ],
                    'apparent_power_s' => [
                        'label' => 'Apparent S',
                        'expression' => 'apparent_power_s',
                        'unit' => 'VA',
                    ],
                    'apparent_power_t' => [
                        'label' => 'Apparent T',
                        'expression' => 'apparent_power_t',
                        'unit' => 'VA',
                    ],
                ],
            ],

            default => [],
        };
    }
}