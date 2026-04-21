<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('rsptn_power_baru', function (Blueprint $table) {
            $table->id();
            $table->decimal('active_power_r', 12, 2)->nullable();
            $table->decimal('active_power_s', 12, 2)->nullable();
            $table->decimal('active_power_t', 12, 2)->nullable();
            $table->decimal('reactive_power_r', 12, 2)->nullable();
            $table->decimal('reactive_power_s', 12, 2)->nullable();
            $table->decimal('reactive_power_t', 12, 2)->nullable();
            $table->decimal('apparent_power_r', 12, 2)->nullable();
            $table->decimal('apparent_power_s', 12, 2)->nullable();
            $table->decimal('apparent_power_t', 12, 2)->nullable();
            $table->timestamp('time_stamp')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('rsptn_power_baru');
    }
};
