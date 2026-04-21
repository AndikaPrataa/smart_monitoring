<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Broadcasting\InteractsWithSockets;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class Pm10Updated implements ShouldBroadcast
{
    use Dispatchable, InteractsWithSockets, SerializesModels;
    public $data;
    public function __construct($data) { $this->data = $data; }
    public function broadcastOn(): Channel { return new Channel('monitoring.pm10'); }
    public function broadcastWith(): array { return ['value' => $this->data]; }
    public function broadcastAs(): string { return 'pm10.updated'; }
}
