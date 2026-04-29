<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('notifications', function (Blueprint $table) {
            $table->foreignId('assigned_to')
                ->nullable()
                ->after('status')
                ->constrained('users')
                ->nullOnDelete();

            $table->timestamp('assigned_at')->nullable()->after('assigned_to');
            $table->timestamp('completed_at')->nullable()->after('assigned_at');

            $table->string('field_photo')->nullable()->after('completed_at');
            $table->text('action_taken')->nullable()->after('field_photo');
            $table->text('additional_note')->nullable()->after('action_taken');
        });
    }

    public function down(): void
    {
        Schema::table('notifications', function (Blueprint $table) {
            $table->dropForeign(['assigned_to']);
            $table->dropColumn([
                'assigned_to',
                'assigned_at',
                'completed_at',
                'field_photo',
                'action_taken',
                'additional_note',
            ]);
        });
    }
};