<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Broadcasting\InteractsWithSockets;
use Illuminate\Broadcasting\PresenceChannel;
use Illuminate\Broadcasting\PrivateChannel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class ReactivePowerTUpdated implements ShouldBroadcast
{{
    use Dispatchable, InteractsWithSockets, SerializesModels;

    public $data;

    public function __construct($data)
    {{
        $this->data = $data;
    }}

    public function broadcastOn(): Channel
    {{
        return new Channel('daya.reactive.t');
    }}

    public function broadcastWith(): array
    {{
        return ['value' => $this->data];
    }}

    public function broadcastAs(): string
    {{
        return 'reactive_power_t.updated';
    }}
}}
