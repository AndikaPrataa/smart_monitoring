<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class IEQUpdated implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public array $ieq;

    public function __construct(array $ieq)
    {
        $this->ieq = $ieq;
    }

    public function broadcastOn(): Channel
    {
        return new Channel('monitoring.ieq');
    }

    public function broadcastAs(): string
    {
        return 'ieq.updated';
    }

    public function broadcastWith(): array
    {
        return [
            'ieq' => $this->ieq,
        ];
    }
}