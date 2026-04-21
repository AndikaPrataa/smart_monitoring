<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class MonitoringLingkungan extends Model
{
    protected $table = 'monitoring_lingkungan';
    protected $guarded = [];

    public function getCreatedAtColumn()
    {
        return 'waktu';
    }

    public function getUpdatedAtColumn()
    {
        return null;
    }
}
