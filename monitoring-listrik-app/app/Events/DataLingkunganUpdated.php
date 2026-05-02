<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class DataLingkunganUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public $data;

    public function __construct($data)
    {
        $this->data = $data;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('monitoring.lingkungan');
    }

    public function broadcastAs(): string
    {
        return 'monitoring_lingkungan.updated';
    }

    public function broadcastWith(): array
    {
        return [
            'data' => $this->data,
        ];
    }
}