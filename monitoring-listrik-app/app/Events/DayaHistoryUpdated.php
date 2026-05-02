<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class DayaHistoryUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public array $data;

    public function __construct(array $data)
    {
        $this->data = $data;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('daya.history');
    }

    public function broadcastAs(): string
    {
        return 'daya.history.updated';
    }

    public function broadcastWith(): array
    {
        return $this->data;
    }
}