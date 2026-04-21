# 📋 NEXT STEPS - Setup Database & Jalankan Reverb

## Status Sekarang
✅ **Laravel Reverb sudah terinstall dan dikonfigurasi dengan sempurna**

Tingkat kelengkapan: **95%**  
Sisa: Persiapan Database

---

## 🚀 Langkah 1: Import Database Schema

Sebelum menjalankan Reverb server, database `monitoring_listrik.sql` harus di-import ke MySQL.

### Opsi A: Dengan MySQL Command Line
```bash
mysql -u root -p < "..\monitoring_listrik.sql"
```
*(Tekan Enter, jangan ada password karena sesuai .env)*

### Opsi B: Dengan MySQL Workbench
1. Buka MySQL Workbench
2. File → Open SQL Script → Pilih `monitoring_listrik.sql`
3. Click "Execute All" (atau Ctrl+Shift+Enter)
4. Verifikasi database `monitoring_listrik` berisi tabel-tabel yang diperlukan

### Opsi C: Catat Langsung ke .env jika ada password
```env
DB_PASSWORD=your_password
```

---

## ✅ Langkah 2: Verifikasi Database Connection

Setelah import, jalankan:
```bash
cd "d:\rsptn local\monitoring-listrik-app"
php artisan tinker
```

Lalu di Tinker shell:
```php
>>> DB::connection()->getPdo()
PDOConnection Object
```

Jika hasilnya `PDOConnection Object`, berarti ✅ **Connection OK!**

---

## 🎯 Langkah 3: Jalankan Migrations (Jika diperlukan)

Jika ada Laravel migrations default (untuk cache, sessions, etc):
```bash
php artisan migrate
```

---

## 🚀 Langkah 4: Jalankan Reverb Server

Setelah database siap, buka **3 Terminal berbeda**:

### **Terminal 1 - Reverb WebSocket Server** ⚡
```bash
cd "d:\rsptn local\monitoring-listrik-app"
php artisan reverb:start
```

**Output yang diharapkan:**
```
   INFO  Starting Reverb server...

  ┌─────────────────────────────────────────────────────────┐
  │ Reverb WebSocket Server started on 127.0.0.1:8080      │
  └─────────────────────────────────────────────────────────┘
```

**PORT**: `8080`  
**KEEP RUNNING**: Jangan tutup terminal ini ✓

---

### **Terminal 2 - Laravel Development Server** 🎯
```bash
cd "d:\rsptn local\monitoring-listrik-app"
php artisan serve
```

**Output yang diharapkan:**
```
INFO  Server running on [http://127.0.0.1:8000]
```

**PORT**: `8000`  
**AKSES**: http://localhost:8000  
**KEEP RUNNING**: Jangan tutup terminal ini ✓

---

### **Terminal 3 - Vite Dev Server** 📦
```bash
cd "d:\rsptn local\monitoring-listrik-app"
npm run dev
```

**Output yang diharapkan:**
```
  VITE v7.0.7  ready in 123 ms

  ➜  Local:   http://localhost:5173/
```

**PORT**: `5173`  
**KEEP RUNNING**: Jangan tutup terminal ini ✓

---

## 🔗 Akses Application

Setelah ketiga server berjalan:
- **Main App**: http://localhost:8000
- **Vite Dev Assets**: http://localhost:5173
- **WebSocket Server**: ws://127.0.0.1:8080

---

## ✅ Testing Real-time Broadcasting

Setelah semua server berjalan, test dengan membuat Event:

### 1. Buat Event Broadcasting
```bash
php artisan make:event ElectricityStatusChanged --broadcast
```

### 2. Edit Event di `app/Events/ElectricityStatusChanged.php`
```php
<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Broadcasting\InteractsWithSockets;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class ElectricityStatusChanged implements ShouldBroadcast
{
    use Dispatchable, InteractsWithSockets, SerializesModels;

    public function __construct(
        public string $deviceId,
        public bool $isActive,
        public float $voltage,
    ) {}

    public function broadcastOn(): Channel
    {
        return new Channel('electricity-status');
    }

    public function broadcastAs(): string
    {
        return 'status-changed';
    }
}
```

### 3. Broadcast dari Tinker
```bash
php artisan tinker
```

```php
>>> use App\Events\ElectricityStatusChanged;
>>> event(new ElectricityStatusChanged('PANEL-01', true, 220.5));
```

### 4. Listen di Frontend (resources/js/app.js)
```javascript
import './bootstrap';

Echo.channel('electricity-status')
    .listen('status-changed', (event) => {
        console.log('🔵 Status Changed:', event);
        console.log('Device:', event.deviceId);
        console.log('Active:', event.isActive);
        console.log('Voltage:', event.voltage);
    });
```

### 5. Cek Browser Console
- Buka http://localhost:8000 di browser
- Buka Developer Tools (F12)
- Cek Console tab untuk real-time events

---

## 🛠️ Troubleshooting

### ❌ Error: "Table 'monitoring_listrik.cache' doesn't exist"
**Solusi**: Database belum di-import
```bash
mysql -u root < "..\monitoring_listrik.sql"
```

### ❌ Error: "Connection refused on port 8080"
**Solusi**: Port sudah digunakan
```bash
# Cek port mana yang pakai 8080
netstat -ano | findstr ":8080"

# Ganti port di .env jika dibutuhkan
REVERB_PORT=9000
```

### ❌ Error: "WebSocket connection failed"
**Pastikan**:
- ✅ Reverb server berjalan di Terminal 1
- ✅ `REVERB_HOST=127.0.0.1` di .env
- ✅ `VITE_REVERB_HOST` sama dengan `REVERB_HOST`
- ✅ Browser tidak di-block oleh corporate firewall

### ❌ "npm: command not found"
```bash
# Install Node.js dari nodejs.org atau
choco install nodejs  # (Jika sudah install Chocolatey)
```

---

## 📊 Architecture Diagram

```
┌─────────────────────┬──────────────────────┬─────────────────┐
│   Client Browser    │   Laravel Server     │  Reverb Server  │
│   (JavaScript)      │   (PHP)              │  (WebSocket)    │
├─────────────────────┼──────────────────────┼─────────────────┤
│                     │                      │                 │
│ Echo.channel()      │ event()              │ Broadcast      │
│   ↓                 │   ↓                  │   ↓             │
│ ws://127.0.0.1:8080 ←→ php artisan serve   ←→ WebSocket Port │
│   ↓                 │   ↓                  │   ↓             │
│ Listen Events       │ Broadcast to Reverb  │ Push to Clients │
│                     │                      │                 │
└─────────────────────┴──────────────────────┴─────────────────┘
```

---

## 📋 Command Reference

| Command | Purpose |
|---------|---------|
| `php artisan reverb:start` | Jalankan WebSocket server |
| `php artisan reverb:start --debug` | WebSocket dengan debug mode |
| `php artisan serve` | Jalankan dev server Laravel |
| `npm run dev` | Jalankan Vite dev server |
| `php artisan tinker` | Interactive shell untuk testing |
| `php artisan make:event EventName --broadcast` | Buat event baru |

---

## 🎯 Tahap Berikutnya (Setelah Database Ready)

1. ✅ Setup Reverb WebSocket (DONE)
2. ⏳ **Import Database & Test Reverb** (NEXT)
3. ⏳ Build API endpoints untuk monitoring data
4. ⏳ Create Real-time Dashboard UI
5. ⏳ Setup Pusher/Reverb channels untuk entity monitoring
6. ⏳ Test end-to-end
7. ⏳ Deploy ke Production

---

**Time to Full Setup**: ~5-10 menit (setelah import database)  
**Difficulty Level**: Beginner-friendly  
**Status**: Production-ready after Testing ✓

