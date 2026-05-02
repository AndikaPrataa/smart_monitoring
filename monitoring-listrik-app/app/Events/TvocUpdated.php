<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class TvocUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public $value;

    public function __construct($value)
    {
        $this->value = $value;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('monitoring.tvoc');
    }

    public function broadcastAs(): string
    {
        return 'tvoc.updated';
    }

    public function broadcastWith(): array
    {
        return [
            'value' => $this->value,
        ];
    }
}