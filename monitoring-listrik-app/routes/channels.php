<?php

use Illuminate\Support\Facades\Broadcast;

Broadcast::channel('notification.admin', function ($user) {
    return $user && $user->role === 'admin';
});

Broadcast::channel('notification.technician.{id}', function ($user, $id) {
    return $user && $user->role === 'teknisi' && (int) $user->id === (int) $id;
});