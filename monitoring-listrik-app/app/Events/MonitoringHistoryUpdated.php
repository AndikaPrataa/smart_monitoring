<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class MonitoringHistoryUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public array $data;

    public function __construct(array $data)
    {
        $this->data = $data;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('monitoring.history');
    }

    public function broadcastAs(): string
    {
        return 'monitoring.history.updated';
    }

    public function broadcastWith(): array
    {
        return $this->data;
    }
}