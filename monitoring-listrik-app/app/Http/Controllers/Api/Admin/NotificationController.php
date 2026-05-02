<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use App\Models\User;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $status = $request->query('status');
        $level = $request->query('level');
        $kategori = $request->query('kategori');
        $assignedTo = $request->query('assigned_to');

        $query = Notification::with('technician')
            ->orderBy('created_at', 'desc');

        if ($status) {
            $query->where('status', $status);
        }

        if ($level) {
            $query->where('level', $level);
        }

        if ($kategori) {
            $query->where('kategori', $kategori);
        }

        if ($assignedTo) {
            $query->where('assigned_to', $assignedTo);
        }

        $notifications = $query->get();

        return response()->json([
            'success' => true,
            'message' => 'Daftar notifikasi berhasil diambil',
            'total' => $notifications->count(),
            'data' => $notifications,
        ]);
    }

    public function show($id): JsonResponse
    {
        $notification = Notification::with('technician')->find($id);

        if (!$notification) {
            return response()->json([
                'success' => false,
                'message' => 'Notifikasi tidak ditemukan',
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Detail notifikasi berhasil diambil',
            'data' => $notification,
        ]);
    }

    public function assign(Request $request, $id): JsonResponse
    {
        $validated = $request->validate([
            'assigned_to' => 'required|exists:users,id',
        ]);

        $notification = Notification::find($id);

        if (!$notification) {
            return response()->json([
                'success' => false,
                'message' => 'Notifikasi tidak ditemukan',
            ], 404);
        }

        if ($notification->status === 'selesai') {
            return response()->json([
                'success' => false,
                'message' => 'Notifikasi yang sudah selesai tidak dapat ditugaskan ulang',
            ], 422);
        }

        $technician = User::where('id', $validated['assigned_to'])
            ->where('role', 'teknisi')
            ->first();

        if (!$technician) {
            return response()->json([
                'success' => false,
                'message' => 'User yang dipilih bukan teknisi atau tidak ditemukan',
            ], 422);
        }

        $activeTaskCount = Notification::where('assigned_to', $technician->id)
            ->where('status', 'proses')
            ->count();

        $isSameTechnician = (int) $notification->assigned_to === (int) $technician->id;

        if ($activeTaskCount > 0 && !$isSameTechnician) {
            return response()->json([
                'success' => false,
                'message' => 'Teknisi sedang memiliki tugas aktif dan belum dapat menerima tugas baru',
                'technician' => [
                    'id' => $technician->id,
                    'name' => $technician->name,
                    'email' => $technician->email,
                    'nip' => $technician->nip,
                    'status_teknisi' => 'sibuk',
                    'active_task_count' => $activeTaskCount,
                    'can_receive_task' => false,
                ],
            ], 422);
        }

        $notification->assigned_to = $technician->id;
        $notification->assigned_at = now();
        $notification->status = 'proses';
        $notification->save();

        $notification->refresh();
        $notification->load('technician');

        $activeTaskCountAfterAssign = Notification::where('assigned_to', $technician->id)
            ->where('status', 'proses')
            ->count();

        return response()->json([
            'success' => true,
            'message' => 'Notifikasi berhasil ditugaskan ke teknisi',
            'data' => $notification,
            'technician_status' => [
                'id' => $technician->id,
                'name' => $technician->name,
                'email' => $technician->email,
                'nip' => $technician->nip,
                'status_teknisi' => $activeTaskCountAfterAssign > 0 ? 'sibuk' : 'free',
                'active_task_count' => $activeTaskCountAfterAssign,
                'can_receive_task' => $activeTaskCountAfterAssign === 0,
            ],
        ]);
    }
}