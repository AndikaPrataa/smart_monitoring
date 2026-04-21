<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Broadcasting\InteractsWithSockets;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class Eco2Updated implements ShouldBroadcast
{
    use Dispatchable, InteractsWithSockets, SerializesModels;
    public $data;
    public function __construct($data) { $this->data = $data; }
    public function broadcastOn(): Channel { return new Channel('monitoring.eco2'); }
    public function broadcastWith(): array { return ['value' => $this->data]; }
    public function broadcastAs(): string { return 'eco2.updated'; }
}
