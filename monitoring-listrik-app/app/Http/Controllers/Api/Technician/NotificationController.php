<?php

namespace App\Http\Controllers\Api\Technician;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $user = $request->user();

        $status = $request->query('status');

        $query = Notification::where('assigned_to', $user->id)
            ->orderBy('created_at', 'desc');

        if ($status) {
            if ($status === 'aktif') {
                $query->where('status', 'proses');
            } else {
                $query->where('status', $status);
            }
        }

        $notifications = $query->get();

        return response()->json([
            'success' => true,
            'message' => 'Daftar tugas teknisi berhasil diambil',
            'total' => $notifications->count(),
            'data' => $notifications,
        ]);
    }

    public function show(Request $request, $id): JsonResponse
    {
        $user = $request->user();

        $notification = Notification::where('id', $id)
            ->where('assigned_to', $user->id)
            ->first();

        if (!$notification) {
            return response()->json([
                'success' => false,
                'message' => 'Tugas tidak ditemukan atau bukan milik teknisi ini',
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Detail tugas teknisi berhasil diambil',
            'data' => $notification,
        ]);
    }

    public function complete(Request $request, $id): JsonResponse
    {
        $user = $request->user();

        $validated = $request->validate([
            'action_taken' => 'required|string',
            'additional_note' => 'nullable|string',
            'field_photo' => 'nullable|image|mimes:jpg,jpeg,png|max:2048',
        ]);

        $notification = Notification::where('id', $id)
            ->where('assigned_to', $user->id)
            ->first();

        if (!$notification) {
            return response()->json([
                'success' => false,
                'message' => 'Tugas tidak ditemukan atau bukan milik teknisi ini',
            ], 404);
        }

        if ($notification->status === 'selesai') {
            return response()->json([
                'success' => false,
                'message' => 'Tugas ini sudah diselesaikan sebelumnya',
            ], 422);
        }

        $photoPath = $notification->field_photo;

        if ($request->hasFile('field_photo')) {
            $photoPath = $request->file('field_photo')->store('field-photos', 'public');
        }

        $notification->update([
            'status' => 'selesai',
            'completed_at' => now(),
            'field_photo' => $photoPath,
            'action_taken' => $validated['action_taken'],
            'additional_note' => $validated['additional_note'] ?? null,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Tugas berhasil diselesaikan',
            'data' => $notification,
        ]);
    }
}