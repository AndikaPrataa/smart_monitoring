<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class TotalCurrentUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public $data;

    public function __construct($data)
    {
        $this->data = $data;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('listrik.total.current');
    }

    public function broadcastWith(): array
    {
        return [
            'value' => $this->data,
        ];
    }

    public function broadcastAs(): string
    {
        return 'total_current.updated';
    }
}