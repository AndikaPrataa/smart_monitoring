<?php

namespace Database\Seeders;

use App\Models\MonitoringLingkungan;
use App\Models\RsptdDataBaru;
use App\Models\RsptdPowerBaru;
use Illuminate\Database\Seeder;

class SensorDataSeeder extends Seeder
{
    public function run(): void
    {
        // Seed Monitoring Lingkungan (Environment) data
        MonitoringLingkungan::create([
            'suhu' => 28.5,
            'kelembapan' => 65.2,
            'pm25' => 35.8,
            'pm10' => 52.4,
            'gas_co' => 1.2,
            'gas_co2' => 420.5,
            'cahaya' => 450.0,
            'kebisingan' => 65.8,
            'lokasi' => 'Ruang Server',
            'waktu' => now(),
        ]);

        // Seed Listrik (Electricity) data
        RsptdDataBaru::create([
            'voltage_l1l2' => 230.5,
            'voltage_l2l3' => 231.2,
            'voltage_l3l1' => 229.8,
            'voltage_l1n' => 133.2,
            'voltage_l2n' => 133.5,
            'voltage_l3n' => 132.8,
            'current_l1' => 15.5,
            'current_l2' => 16.2,
            'current_l3' => 14.8,
            'current_n' => 0.5,
            'frecuency' => 50.05,
            'power_factor' => 0.95,
            'time_stamp' => now(),
        ]);

        // Seed Daya (Power) data
        RsptdPowerBaru::create([
            'active_power_r' => 3600.5,
            'active_power_s' => 3850.2,
            'active_power_t' => 3450.8,
            'reactive_power_r' => 850.5,
            'reactive_power_s' => 920.2,
            'reactive_power_t' => 800.8,
            'apparent_power_r' => 3750.2,
            'apparent_power_s' => 4050.8,
            'apparent_power_t' => 3600.5,
            'time_stamp' => now(),
        ]);
    }
}
