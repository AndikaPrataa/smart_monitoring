<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use App\Models\User;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\Rule;

class AdminUserController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $role = $request->query('role');

        $query = User::query()
            ->select('id', 'name', 'email', 'nip', 'role', 'created_at', 'updated_at')
            ->orderBy('created_at', 'desc');

        if ($role) {
            if (!in_array($role, ['admin', 'teknisi'], true)) {
                return response()->json([
                    'success' => false,
                    'message' => 'Role tidak valid',
                    'allowed_roles' => ['admin', 'teknisi'],
                ], 422);
            }

            $query->where('role', $role);
        }

        $users = $query->get()->map(function ($user) {
            if ($user->role === 'teknisi') {
                $activeTaskCount = Notification::where('assigned_to', $user->id)
                    ->where('status', 'proses')
                    ->count();

                $totalAssignedTaskCount = Notification::where('assigned_to', $user->id)
                    ->count();

                $user->active_task_count = $activeTaskCount;
                $user->total_assigned_task_count = $totalAssignedTaskCount;
                $user->status_teknisi = $activeTaskCount > 0 ? 'sibuk' : 'free';
                $user->can_receive_task = $activeTaskCount === 0;
            } else {
                $user->active_task_count = null;
                $user->total_assigned_task_count = null;
                $user->status_teknisi = null;
                $user->can_receive_task = null;
            }

            return $user;
        });

        return response()->json([
            'success' => true,
            'message' => 'Daftar user berhasil diambil',
            'total' => $users->count(),
            'data' => $users,
        ]);
    }

    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users,email',
            'nip' => 'nullable|string|max:50|unique:users,nip',
            'password' => 'required|string|min:6',
            'role' => [
                'required',
                Rule::in(['admin', 'teknisi']),
            ],
        ]);

        $user = User::create([
            'name' => $validated['name'],
            'email' => $validated['email'],
            'nip' => $validated['nip'] ?? null,
            'password' => Hash::make($validated['password']),
            'role' => $validated['role'],
        ]);

        return response()->json([
            'success' => true,
            'message' => 'User berhasil dibuat',
            'data' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'nip' => $user->nip,
                'role' => $user->role,
                'created_at' => $user->created_at,
            ],
        ], 201);
    }

    public function teknisi(): JsonResponse
    {
        $teknisi = User::query()
            ->select('id', 'name', 'email', 'nip', 'role', 'created_at', 'updated_at')
            ->where('role', 'teknisi')
            ->orderBy('name', 'asc')
            ->get()
            ->map(function ($user) {
                $activeTaskCount = Notification::where('assigned_to', $user->id)
                    ->where('status', 'proses')
                    ->count();

                $totalAssignedTaskCount = Notification::where('assigned_to', $user->id)
                    ->count();

                $user->active_task_count = $activeTaskCount;
                $user->total_assigned_task_count = $totalAssignedTaskCount;
                $user->status_teknisi = $activeTaskCount > 0 ? 'sibuk' : 'free';
                $user->can_receive_task = $activeTaskCount === 0;

                return $user;
            });

        return response()->json([
            'success' => true,
            'message' => 'Daftar teknisi berhasil diambil',
            'total' => $teknisi->count(),
            'data' => $teknisi,
        ]);
    }

    public function update(Request $request, int $id): JsonResponse
    {
        $user = User::find($id);

        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak ditemukan',
            ], 404);
        }

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => [
                'required',
                'string',
                'email',
                'max:255',
                Rule::unique('users', 'email')->ignore($user->id),
            ],
            'nip' => [
                'nullable',
                'string',
                'max:50',
                Rule::unique('users', 'nip')->ignore($user->id),
            ],
            'password' => 'nullable|string|min:6',
            'role' => [
                'required',
                Rule::in(['admin', 'teknisi']),
            ],
        ]);

        $user->name = $validated['name'];
        $user->email = $validated['email'];
        $user->nip = $validated['nip'] ?? null;
        $user->role = $validated['role'];

        if (!empty($validated['password'])) {
            $user->password = Hash::make($validated['password']);
        }

        $user->save();

        return response()->json([
            'success' => true,
            'message' => 'User berhasil diperbarui',
            'data' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'nip' => $user->nip,
                'role' => $user->role,
                'updated_at' => $user->updated_at,
            ],
        ]);
    }

    public function destroy(int $id): JsonResponse
    {
        $user = User::find($id);

        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak ditemukan',
            ], 404);
        }

        $user->delete();

        return response()->json([
            'success' => true,
            'message' => 'User berhasil dihapus',
        ]);
    }
}