<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use App\Models\User;
use App\Events\NotificationUpdated;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    public function index(Request $request)
    {
        $query = Notification::with('technician')
            ->latest();

        if ($request->has('status')) {
            $query->where('status', $request->status);
        }

        return response()->json([
            'success' => true,
            'message' => 'Daftar peringatan berhasil diambil',
            'data' => $query->get()
        ]);
    }

    public function show($id)
    {
        $notification = Notification::with('technician')->findOrFail($id);

        return response()->json([
            'success' => true,
            'message' => 'Detail peringatan berhasil diambil',
            'data' => $notification
        ]);
    }

    public function assign(Request $request, $id)
    {
        $request->validate([
            'technician_id' => 'required|exists:users,id',
        ]);

        $technician = User::where('id', $request->technician_id)
            ->where('role', 'teknisi')
            ->first();

        if (!$technician) {
            return response()->json([
                'success' => false,
                'message' => 'User yang dipilih bukan teknisi'
            ], 422);
        }

        $notification = Notification::findOrFail($id);

        if ($notification->status !== 'aktif') {
            return response()->json([
                'success' => false,
                'message' => 'Peringatan hanya bisa diteruskan jika status masih aktif'
            ], 422);
        }

        $notification->update([
            'assigned_to' => $technician->id,
            'assigned_at' => now(),
            'status' => 'proses',
        ]);

        $notification->refresh();
        $notification->load('technician');

        event(new NotificationUpdated($notification));

        return response()->json([
            'success' => true,
            'message' => 'Peringatan berhasil diteruskan ke teknisi',
            'data' => $notification
        ]);
    }
}