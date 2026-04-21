<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create Admin user
        User::create([
            'name' => 'Admin User',
            'email' => 'admin@example.com',
            'password' => Hash::make('admin1234'),
            'role' => 'admin',
        ]);

        // Create Teknisi user
        User::create([
            'name' => 'Teknisi User',
            'email' => 'teknisi@example.com',
            'password' => Hash::make('teknisi1234'),
            'role' => 'teknisi',
        ]);

        // Create Alya Nayra user (updated with role)
        User::create([
            'name' => 'Alya Nayra Syafiqa',
            'email' => 'alyaayra@gmail.com',
            'password' => Hash::make('alya1234'),
            'role' => 'admin',
        ]);
    }
}
