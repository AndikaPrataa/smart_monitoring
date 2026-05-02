<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class MonitoringLingkungan extends Model
{
    protected $table = 'monitoring_lingkungan';

    protected $fillable = [
        'suhu',
        'kelembapan',
        'pm25',
        'pm10',
        'gas_co',
        'gas_co2',
        'tvoc',
        'cahaya',
        'kebisingan',
        'lokasi',
    ];

    const CREATED_AT = 'waktu';
    const UPDATED_AT = null;

    protected $casts = [
        'waktu' => 'datetime',

        'suhu' => 'float',
        'kelembapan' => 'float',
        'pm25' => 'float',
        'pm10' => 'float',
        'gas_co' => 'float',
        'gas_co2' => 'float',
        'tvoc' => 'float',
        'cahaya' => 'float',
        'kebisingan' => 'float',
    ];
}