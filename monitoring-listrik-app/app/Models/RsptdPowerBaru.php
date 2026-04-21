<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class RsptdPowerBaru extends Model
{
    protected $table = 'rsptn_power_baru';
    protected $guarded = [];

    public function getCreatedAtColumn()
    {
        return 'time_stamp';
    }

    public function getUpdatedAtColumn()
    {
        return 'time_stamp';
    }
}
