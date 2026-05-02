<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class RsptdDataBaru extends Model
{
    protected $table = 'rsptn_data_baru';

    protected $fillable = [
        'voltage_l1l2',
        'voltage_l2l3',
        'voltage_l3l1',
        'voltage_l1n',
        'voltage_l2n',
        'voltage_l3n',

        'current_l1',
        'current_l2',
        'current_l3',
        'current_n',

        'frecuency',
        'power_factor',
    ];

    const CREATED_AT = 'time_stamp';
    const UPDATED_AT = null;

    protected $casts = [
        'time_stamp' => 'datetime',

        'voltage_l1l2' => 'float',
        'voltage_l2l3' => 'float',
        'voltage_l3l1' => 'float',
        'voltage_l1n' => 'float',
        'voltage_l2n' => 'float',
        'voltage_l3n' => 'float',

        'current_l1' => 'float',
        'current_l2' => 'float',
        'current_l3' => 'float',
        'current_n' => 'float',

        'frecuency' => 'float',
        'power_factor' => 'float',
    ];
}