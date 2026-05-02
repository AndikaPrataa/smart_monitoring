<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class BiayaDayaUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public array $data;

    public function __construct(array $data)
    {
        $this->data = $data;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('daya.biaya');
    }

    public function broadcastAs(): string
    {
        return 'daya.biaya.updated';
    }

    public function broadcastWith(): array
    {
        return $this->data;
    }
}