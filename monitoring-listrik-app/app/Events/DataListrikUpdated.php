<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class DataListrikUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public $data;

    public function __construct($data)
    {
        $this->data = $data;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('listrik');
    }

    public function broadcastAs(): string
    {
        return 'listrik.updated';
    }

    public function broadcastWith(): array
    {
        return [
            'data' => $this->data,
        ];
    }
}