<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class RsptdPowerBaru extends Model
{
    protected $table = 'rsptn_power_baru';

    protected $fillable = [
        'active_power_r',
        'active_power_s',
        'active_power_t',

        'reactive_power_r',
        'reactive_power_s',
        'reactive_power_t',

        'apparent_power_r',
        'apparent_power_s',
        'apparent_power_t',
    ];

    const CREATED_AT = 'time_stamp';
    const UPDATED_AT = null;

    protected $casts = [
        'time_stamp' => 'datetime',

        'active_power_r' => 'float',
        'active_power_s' => 'float',
        'active_power_t' => 'float',

        'reactive_power_r' => 'float',
        'reactive_power_s' => 'float',
        'reactive_power_t' => 'float',

        'apparent_power_r' => 'float',
        'apparent_power_s' => 'float',
        'apparent_power_t' => 'float',
    ];
}