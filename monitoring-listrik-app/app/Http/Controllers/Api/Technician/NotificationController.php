<?php

namespace App\Http\Controllers\Api\Technician;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use App\Events\NotificationUpdated;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class NotificationController extends Controller
{
    public function index(Request $request)
    {
        $user = $request->user();

        $query = Notification::where('assigned_to', $user->id)
            ->orderBy('timestamp', 'desc');

        if ($request->status) {
            if ($request->status === 'aktif') {
                $query->where('status', 'proses');
            } elseif ($request->status === 'selesai') {
                $query->where('status', 'selesai');
            }
        } else {
            $query->whereIn('status', ['proses', 'selesai']);
        }

        $data = $query->get()->map(function ($item) {
            $item->status_asli = $item->status;
            $item->status_label = $item->status === 'proses'
                ? 'aktif'
                : $item->status;

            return $item;
        });

        return response()->json([
            'success' => true,
            'message' => 'Daftar tugas teknisi berhasil diambil',
            'data' => $data
        ]);
    }

    public function show(Request $request, $id)
    {
        $notification = Notification::where('assigned_to', $request->user()->id)
            ->where('id', $id)
            ->firstOrFail();

        $notification->status_asli = $notification->status;
        $notification->status_label = $notification->status === 'proses'
            ? 'aktif'
            : $notification->status;

        return response()->json([
            'success' => true,
            'message' => 'Detail tugas berhasil diambil',
            'data' => $notification
        ]);
    }

    public function complete(Request $request, $id)
    {
        $request->validate([
            'field_photo' => 'required|image|mimes:jpg,jpeg,png,webp|max:2048',
            'action_taken' => 'required|string',
            'additional_note' => 'nullable|string',
        ]);

        $notification = Notification::where('assigned_to', $request->user()->id)
            ->where('status', 'proses')
            ->where('id', $id)
            ->firstOrFail();

        if ($notification->field_photo && Storage::disk('public')->exists($notification->field_photo)) {
            Storage::disk('public')->delete($notification->field_photo);
        }

        $photoPath = $request->file('field_photo')
            ->store('notification_photos', 'public');

        $notification->update([
            'status' => 'selesai',
            'completed_at' => now(),
            'field_photo' => $photoPath,
            'action_taken' => $request->action_taken,
            'additional_note' => $request->additional_note,
        ]);

        $notification->refresh();

        $notification->status_asli = $notification->status;
        $notification->status_label = 'selesai';

        event(new NotificationUpdated($notification));

        return response()->json([
            'success' => true,
            'message' => 'Penanganan berhasil dikonfirmasi',
            'data' => $notification
        ]);
    }
}