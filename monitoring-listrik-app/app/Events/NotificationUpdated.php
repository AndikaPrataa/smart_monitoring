<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class NotificationUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public $notification;

    public function __construct($notification)
    {
        $this->notification = $notification;
    }

    public function broadcastOn(): array
    {
        return [
            new Channel('notification.admin'),
            new Channel('notification.technician.' . $this->notification->assigned_to),
        ];
    }

    public function broadcastAs(): string
    {
        return 'notification.updated';
    }

    public function broadcastWith(): array
    {
        return [
            'notification' => $this->notification,
        ];
    }
}