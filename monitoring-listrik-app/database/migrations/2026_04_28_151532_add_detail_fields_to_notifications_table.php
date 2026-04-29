<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('notifications', function (Blueprint $table) {
            if (!Schema::hasColumn('notifications', 'kategori')) {
                $table->string('kategori')->nullable()->after('id');
            }

            if (!Schema::hasColumn('notifications', 'title')) {
                $table->string('title')->nullable()->after('sensor');
            }

            if (!Schema::hasColumn('notifications', 'value')) {
                $table->float('value')->nullable()->after('level');
            }

            if (!Schema::hasColumn('notifications', 'unit')) {
                $table->string('unit')->nullable()->after('value');
            }
        });
    }

    public function down(): void
    {
        Schema::table('notifications', function (Blueprint $table) {
            $table->dropColumn([
                'kategori',
                'title',
                'value',
                'unit',
            ]);
        });
    }
};