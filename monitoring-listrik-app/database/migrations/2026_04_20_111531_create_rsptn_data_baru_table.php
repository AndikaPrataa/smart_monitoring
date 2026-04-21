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
        Schema::create('rsptn_data_baru', function (Blueprint $table) {
            $table->id();
            $table->decimal('voltage_l1l2', 8, 2)->nullable();
            $table->decimal('voltage_l2l3', 8, 2)->nullable();
            $table->decimal('voltage_l3l1', 8, 2)->nullable();
            $table->decimal('voltage_l1n', 8, 2)->nullable();
            $table->decimal('voltage_l2n', 8, 2)->nullable();
            $table->decimal('voltage_l3n', 8, 2)->nullable();
            $table->decimal('current_l1', 8, 2)->nullable();
            $table->decimal('current_l2', 8, 2)->nullable();
            $table->decimal('current_l3', 8, 2)->nullable();
            $table->decimal('current_n', 8, 2)->nullable();
            $table->decimal('frecuency', 8, 2)->nullable();
            $table->decimal('power_factor', 8, 2)->nullable();
            $table->timestamp('time_stamp')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('rsptn_data_baru');
    }
};
