<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('notifications', function (Blueprint $table) {
            $table->id();
            $table->string('sensor');
            $table->string('level'); // waspada / bahaya
            $table->text('message');
            $table->string('lokasi')->nullable();
            $table->string('status')->default('aktif'); // aktif / selesai
            $table->timestamp('timestamp')->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('notifications');
    }
};