<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\Rule;

class AdminUserController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        if ($request->user()->role !== 'admin') {
            return response()->json([
                'success' => false,
                'message' => 'Akses ditolak. Hanya admin yang dapat melihat daftar pengguna.',
            ], 403);
        }

        $users = User::select('id', 'name', 'nip', 'email', 'role', 'created_at')
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Daftar pengguna berhasil diambil',
            'data' => $users,
        ]);
    }
    public function teknisi(): JsonResponse
{
    $users = User::select('id', 'name', 'nip', 'email', 'role', 'created_at')
        ->where('role', 'teknisi')
        ->orderBy('created_at', 'desc')
        ->get();

    return response()->json([
        'success' => true,
        'message' => 'Daftar teknisi berhasil diambil',
        'data' => $users,
    ]);
}

    public function store(Request $request): JsonResponse
    {
        if ($request->user()->role !== 'admin') {
            return response()->json([
                'success' => false,
                'message' => 'Akses ditolak. Hanya admin yang dapat membuat pengguna.',
            ], 403);
        }

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'nip' => 'required|string|max:50|unique:users,nip',
            'role' => [
                'required',
                'string',
                Rule::in(['admin', 'operator', 'teknisi', 'viewer']),
            ],
            'password' => 'required|string|min:6',
        ]);

        $emailOtomatis = $validated['nip'] . '@local.system';

        $user = User::create([
            'name' => $validated['name'],
            'nip' => $validated['nip'],
            'email' => $emailOtomatis,
            'role' => $validated['role'],
            'password' => Hash::make($validated['password']),
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Pengguna berhasil dibuat',
            'data' => [
                'id' => $user->id,
                'name' => $user->name,
                'nip' => $user->nip,
                'email' => $user->email,
                'role' => $user->role,
            ],
        ], 201);
    }
}