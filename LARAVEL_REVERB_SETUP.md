# Laravel Reverb Setup - Monitoring Listrik & Lingkungan

## 📋 Status Instalasi: ✅ SEMPURNA

Setup Laravel WebSocket dengan **Laravel Reverb** berhasil diinstall dan dikonfigurasi dengan benar untuk project monitoring listrik dan lingkungan.

---

## 🎯 Detail Instalasi

### Framework & Version
- **Laravel Framework**: 12.56.0 (Latest Stable)
- **Laravel Reverb**: v1.10.0 (Official WebSocket Solution)
- **Broadcasting Driver**: Reverb (Default)

### Package Dependencies Terinstall
```
✅ laravel/reverb (^1.10.0) - WebSocket Server
✅ laravel-echo (^2.3.4) - Client-side Broadcasting
✅ pusher-js (^8.5.0) - WebSocket Transport Layer
✅ ratchet/rfc6455 - RFC6455 WebSocket Protocol
✅ react/socket (^1.17.0) - Async Socket Library
✅ react/event-loop (^1.6.0) - Non-blocking I/O
```

---

## ⚙️ Konfigurasi

### 1. Broadcasting Configuration (`config/broadcasting.php`)
✅ **Status**: Sudah dikonfigurasi dengan Reverb
```php
'default' => env('BROADCAST_CONNECTION', 'reverb'),

'connections' => [
    'reverb' => [
        'driver' => 'reverb',
        'key' => env('REVERB_APP_KEY'),
        'secret' => env('REVERB_APP_SECRET'),
        'app_id' => env('REVERB_APP_ID'),
        'options' => [
            'host' => env('REVERB_HOST'),
            'port' => env('REVERB_PORT', 443),
            'scheme' => env('REVERB_SCHEME', 'https'),
        ],
    ],
]
```

### 2. Environment Variables (`.env`)
✅ **Status**: Sudah dikonfigurasi untuk Development

```env
# Broadcasting
BROADCAST_CONNECTION=reverb

# Reverb Server Configuration
REVERB_APP_ID=398367
REVERB_APP_KEY=efksfxfxxfrzuetobo5f
REVERB_APP_SECRET=kbxxkov3px6ewm2nuxlt
REVERB_HOST="localhost"
REVERB_PORT=8080
REVERB_SCHEME=http

# Vite Environment (for client-side)
VITE_REVERB_APP_KEY="${REVERB_APP_KEY}"
VITE_REVERB_HOST="${REVERB_HOST}"
VITE_REVERB_PORT="${REVERB_PORT}"
VITE_REVERB_SCHEME="${REVERB_SCHEME}"

# Database Configuration - MySQL
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=monitoring_listrik
DB_USERNAME=root
DB_PASSWORD=
```

### 3. Client-Side Configuration (`resources/js/echo.js`)
✅ **Status**: Sudah dikonfigurasi dengan Reverb
```javascript
window.Echo = new Echo({
    broadcaster: 'reverb',
    key: import.meta.env.VITE_REVERB_APP_KEY,
    wsHost: import.meta.env.VITE_REVERB_HOST,
    wsPort: import.meta.env.VITE_REVERB_PORT ?? 80,
    wssPort: import.meta.env.VITE_REVERB_PORT ?? 443,
    forceTLS: (import.meta.env.VITE_REVERB_SCHEME ?? 'https') === 'https',
    enabledTransports: ['ws', 'wss'],
});
```

### 4. Broadcasting Channels (`routes/channels.php`)
✅ **Status**: Default channel sudah ada
```php
Broadcast::channel('App.Models.User.{id}', function ($user, $id) {
    return (int) $user->id === (int) $id;
});
```

---

## 🚀 Cara Menjalankan

### 1. **Mulai Reverb WebSocket Server**
```bash
php artisan reverb:start
```

**Opsi untuk Development:**
```bash
# Dengan debug mode
php artisan reverb:start --debug

# Custom host & port
php artisan reverb:start --host=0.0.0.0 --port=8080

# Dengan auto-reload (install watchdog terlebih dahulu)
php artisan reverb:start --watch
```

### 2. **Jalankan Laravel Development Server** (Terminal Lain)
```bash
php artisan serve
```

### 3. **Build Frontend Assets** (Terminal Lain)
```bash
npm run dev
```

### 4. **Struktur Terminal yang Ideal**
```
Terminal 1: php artisan reverb:start          # WebSocket Server (port 8080)
Terminal 2: php artisan serve                 # Laravel Server (port 8000)
Terminal 3: npm run dev                       # Vite Dev Server (port 5173)
```

---

## 📡 Broadcast Events - Quick Start

### Membuat Event Broadcasting
```bash
php artisan make:event ElectricityDataUpdated --broadcast
```

### Contoh Event untuk Monitoring Listrik
```php
// app/Events/ElectricityDataUpdated.php
namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Broadcasting\InteractsWithSockets;
use Illuminate\Broadcasting\PresenceChannel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class ElectricityDataUpdated implements ShouldBroadcast
{
    use Dispatchable, InteractsWithSockets, SerializesModels;

    public function __construct(public array $data) {}

    public function broadcastOn(): Channel
    {
        return new Channel('electricity-monitoring');
    }

    public function broadcastAs(): string
    {
        return 'data-updated';
    }
}
```

### Broadcast dari Controller
```php
use App\Events\ElectricityDataUpdated;

event(new ElectricityDataUpdated([
    'voltage' => 220.5,
    'current' => 15.3,
    'power' => 3370.65,
    'timestamp' => now(),
]));
```

### Listen di Frontend (Blade View)
```javascript
<script>
    // Subscribe ke channel
    Echo.channel('electricity-monitoring')
        .listen('data-updated', (event) => {
            console.log('Data Updated:', event.data);
            // Update UI dengan data terbaru
            updateDashboard(event.data);
        });
</script>
```

---

## 🔐 Autentikasi Channel

### Private Channel (Authorized Users Only)
Edit `routes/channels.php`:
```php
Broadcast::channel('user.{id}.notifications', function ($user, $id) {
    return (int) $user->id === (int) $id;
});
```

Broadcast ke Private Channel:
```php
event(new ElectricityDataUpdated())->toOthers(); // Atau dikirim ke private channel
```

---

## 📊 Database Integration

Database `monitoring_listrik` sudah dikonfigurasi di `.env`:
```
DB_CONNECTION=mysql
DB_DATABASE=monitoring_listrik
```

**Langkah Selanjutnya:**
1. Import `monitoring_listrik.sql` ke MySQL:
   ```bash
   mysql -u root monitoring_listrik < monitoring_listrik.sql
   ```

2. Jalankan migrations (jika ada):
   ```bash
   php artisan migrate
   ```

3. Seed database dengan data awal (jika diperlukan):
   ```bash
   php artisan db:seed
   ```

---

## ✅ Checklist Verifikasi

- [x] Laravel Reverb v1.10.0 terinstall
- [x] Broadcasting Config di `config/broadcasting.php` ✓
- [x] Environment Variables di `.env` ✓
- [x] Laravel Echo client library terpasang
- [x] Pusher JS transport layer ✓
- [x] Routes channels sudah ada ✓
- [x] MySQL database monitoring_listrik dikonfigurasi
- [x] Vite assets compilation ready
- [x] `reverb:start` command tersedia

---

## 🎯 Next Steps (Tahap Berikutnya)

1. **Import Database Schema**
   ```bash
   mysql -u root monitoring_listrik < ../monitoring_listrik.sql
   ```

2. **Create Models & Controllers** untuk entity di database

3. **Setup Real-time Data Sync** dengan event broadcasting

4. **Build Dashboard UI** dengan React/Alpine.js untuk monitoring real-time

5. **Configure for Production** dengan HTTPS/WSS

---

## 📝 File-File Penting

| File | Status | Deskripsi |
|------|--------|-----------|
| `config/broadcasting.php` | ✅ | Konfigurasi broadcast driver |
| `routes/channels.php` | ✅ | Channel authorization |
| `resources/js/echo.js` | ✅ | Client-side Echo setup |
| `resources/js/bootstrap.js` | ✅ | Bootstrap includes echo |
| `package.json` | ✅ | Node dependencies termasuk laravel-echo & pusher-js |
| `.env` | ✅ | Environment variables (Reverb + MySQL) |

---

## 🛠️ Troubleshooting

### Port 8080 Sudah Digunakan?
```bash
php artisan reverb:start --port=9000
```

### WebSocket Connection Failed?
1. Pastikan `REVERB_HOST` dan `REVERB_PORT` benar di `.env`
2. Cek firewall tidak memblokir port WebSocket
3. Actifkan debug mode: `php artisan reverb:start --debug`

### Database Connection Error?
```bash
# Test connection
php artisan tinker
>>> DB::connection()->getPdo()
```

---

## 📚 Dokumentasi Referensi

- [Laravel Reverb Documentation](https://laravel.com/docs/11.x/reverb)
- [Laravel Broadcasting](https://laravel.com/docs/11.x/broadcasting)
- [Laravel Echo Documentation](https://laravel.com/docs/11.x/echo)

---

**Setup Date**: April 20, 2026  
**Status**: ✅ Production Ready (Development Mode)  
**Version**: Laravel 12.56.0 + Reverb 1.10.0

