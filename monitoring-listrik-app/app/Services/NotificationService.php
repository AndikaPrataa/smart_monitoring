<?php

namespace App\Services;

use App\Models\Notification;
use App\Events\NotificationUpdated;

class NotificationService
{
    public static function createIfNotExists(array $notif): ?Notification
    {
        $existing = Notification::where('sensor', $notif['sensor'])
            ->where('lokasi', $notif['lokasi'])
            ->whereIn('status', ['aktif', 'proses'])
            ->first();

        if ($existing) {
            return null;
        }

        $notification = Notification::create([
            'kategori' => $notif['kategori'] ?? null,
            'sensor' => $notif['sensor'],
            'title' => $notif['title'] ?? ucfirst($notif['level']),
            'level' => $notif['level'],
            'value' => $notif['value'] ?? null,
            'unit' => $notif['unit'] ?? null,
            'message' => $notif['message'],
            'lokasi' => $notif['lokasi'],
            'status' => 'aktif',
            'timestamp' => $notif['timestamp'] ?? now(),
        ]);

        event(new NotificationUpdated($notification->fresh()));

        return $notification;
    }

    public static function createMany(array $notifications): array
    {
        $created = [];

        foreach ($notifications as $notif) {
            $newNotification = self::createIfNotExists($notif);

            if ($newNotification) {
                $created[] = $newNotification;
            }
        }

        return $created;
    }
}