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
        Schema::create('monitoring_lingkungan', function (Blueprint $table) {
            $table->id();
            $table->decimal('suhu', 8, 2)->nullable();
            $table->decimal('kelembapan', 8, 2)->nullable();
            $table->decimal('pm25', 8, 2)->nullable();
            $table->decimal('pm10', 8, 2)->nullable();
            $table->decimal('gas_co', 8, 2)->nullable();
            $table->decimal('gas_co2', 8, 2)->nullable();
            $table->decimal('cahaya', 8, 2)->nullable();
            $table->decimal('kebisingan', 8, 2)->nullable();
            $table->string('lokasi')->nullable();
            $table->timestamp('waktu')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('monitoring_lingkungan');
    }
};
