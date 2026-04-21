<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class CheckRole
{
    /**
     * Handle an incoming request.
     *
     * @param  Closure(Request): (Response)  $next
     */
    public function handle(Request $request, Closure $next, $requiredRole = null): Response
    {
        // Jika tidak ada role yang diperiksa, lanjutkan
        if (!$requiredRole) {
            return $next($request);
        }

        $user = $request->user();

        // Jika user tidak authenticated
        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthenticated',
            ], 401);
        }

        // Jika user tidak punya role yang diperlukan
        if ($user->role !== $requiredRole) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized - Anda tidak memiliki akses sebagai ' . $requiredRole,
            ], 403);
        }

        return $next($request);
    }
}
