<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Storage;

class Notification extends Model
{
    protected $fillable = [
        'kategori',
        'sensor',
        'title',
        'level',
        'value',
        'unit',
        'message',
        'lokasi',
        'status',
        'timestamp',

        'assigned_to',
        'assigned_at',

        // fitur teknisi
        'completed_at',
        'field_photo',
        'action_taken',
        'additional_note',
    ];

    protected $casts = [
        'timestamp' => 'datetime',
        'assigned_at' => 'datetime',
        'completed_at' => 'datetime',
    ];

    protected $appends = ['field_photo_url'];

    public function technician()
    {
        return $this->belongsTo(User::class, 'assigned_to');
    }

    public function getFieldPhotoUrlAttribute()
    {
        if (!$this->field_photo) {
            return null;
        }

        return url('storage/' . $this->field_photo);
    }
}