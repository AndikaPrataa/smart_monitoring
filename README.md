# README Backend API & WebSocket  
## Sistem Monitoring Listrik dan Lingkungan

Dokumentasi ini dibuat sebagai panduan integrasi frontend **React** dan mobile **Kotlin** dengan backend Laravel. Backend menyediakan dua jalur komunikasi:

1. **REST API** untuk mengambil data awal, history, filter, login, admin, teknisi, chart, dan estimasi.
2. **WebSocket / Laravel Reverb** untuk update realtime tanpa refresh, seperti nilai sensor terbaru, chart bergerak, history bertambah otomatis, dan biaya realtime.

---

# Base URL

Backend berjalan di IP laptop:

```text
192.168.101.75

Rest API
http://192.168.101.75:8000/api

Websocket 
ws://192.168.101.75:8080 

Menjalankan Backend
php artisan serve --host=0.0.0.0 --port=8000 

Menjalankan WS
php artisan reverb:start --host=0.0.0.0 --port=8080

REST API    = data awal, history lama, filter, chart awal, login, admin, teknisi
WebSocket   = data baru yang masuk secara realtime

# Konfigurasi React untuk REST API dan WebSocket
1. Buat file .env di project React:

VITE_API_BASE_URL=http://192.168.101.75:8000/api

VITE_REVERB_APP_KEY=efksfxfxxfrzuetobo5f
VITE_REVERB_HOST=192.168.101.75
VITE_REVERB_PORT=8080
VITE_REVERB_SCHEME=http

2. Install package
npm install axios laravel-echo pusher-js 

3. Buat konfigurasi Echo

Buat file:
src/echo.js

isi : 
import Echo from 'laravel-echo';
import Pusher from 'pusher-js';

window.Pusher = Pusher;

const echo = new Echo({
  broadcaster: 'reverb',
  key: import.meta.env.VITE_REVERB_APP_KEY,
  wsHost: import.meta.env.VITE_REVERB_HOST,
  wsPort: Number(import.meta.env.VITE_REVERB_PORT),
  wssPort: Number(import.meta.env.VITE_REVERB_PORT),
  forceTLS: false,
  enabledTransports: ['ws'],
});

export default echo;

4. Buat konfigurasi Axios
Buat file:
src/api.js

Isi:
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
});

export default api;

yang butuh token : api.defaults.headers.common.Authorization = `Bearer ${token}`;

Konfigurasi Kotlin Mobile
1. REST API menggunakan Retrofit

Base URL:
const val BASE_URL = "http://192.168.101.75:8000/api/"

2. WebSocket menggunakan Pusher Java Client
Contoh konfigurasi : 
val options = PusherOptions()
options.setHost("192.168.101.75")
options.setWsPort(8080)
options.setWssPort(8080)
options.isUseTLS = false

val pusher = Pusher("efksfxfxxfrzuetobo5f", options)

val channel = pusher.subscribe("monitoring.suhu")

channel.bind("suhu.updated") { event ->
    Log.d("REALTIME", event.data)
}

pusher.connect()

Berikut **URL yang dipakai frontend React/Kotlin** dan **cara memakai WebSocket-nya**.

Base URL API:

```text
http://192.168.101.75:8000/api
```

WebSocket/Reverb:

```text
ws://192.168.101.75:8080
```

---

# 1. URL utama untuk data awal dashboard

## Monitoring lingkungan

```http
GET http://192.168.101.75:8000/api/monitoring/all
```

Dipakai untuk ambil data terbaru semua indikator lingkungan.

Per indikator:

```http
GET http://192.168.101.75:8000/api/monitoring/suhu
GET http://192.168.101.75:8000/api/monitoring/kelembapan
GET http://192.168.101.75:8000/api/monitoring/pm25
GET http://192.168.101.75:8000/api/monitoring/pm10
GET http://192.168.101.75:8000/api/monitoring/gas-co
GET http://192.168.101.75:8000/api/monitoring/gas-co2
GET http://192.168.101.75:8000/api/monitoring/tvoc
GET http://192.168.101.75:8000/api/monitoring/cahaya
GET http://192.168.101.75:8000/api/monitoring/kebisingan
GET http://192.168.101.75:8000/api/monitoring/ieq
```

---

## Listrik

```http
GET http://192.168.101.75:8000/api/listrik/all
```

Per indikator:

```http
GET http://192.168.101.75:8000/api/listrik/voltage-l1l2
GET http://192.168.101.75:8000/api/listrik/voltage-l2l3
GET http://192.168.101.75:8000/api/listrik/voltage-l3l1

GET http://192.168.101.75:8000/api/listrik/current-l1
GET http://192.168.101.75:8000/api/listrik/current-l2
GET http://192.168.101.75:8000/api/listrik/current-l3

GET http://192.168.101.75:8000/api/listrik/total-voltage
GET http://192.168.101.75:8000/api/listrik/total-current
GET http://192.168.101.75:8000/api/listrik/frecuency
GET http://192.168.101.75:8000/api/listrik/power-factor
```

---

## Daya

```http
GET http://192.168.101.75:8000/api/daya/all
```

Per indikator:

```http
GET http://192.168.101.75:8000/api/daya/active-power-r
GET http://192.168.101.75:8000/api/daya/active-power-s
GET http://192.168.101.75:8000/api/daya/active-power-t

GET http://192.168.101.75:8000/api/daya/reactive-power-r
GET http://192.168.101.75:8000/api/daya/reactive-power-s
GET http://192.168.101.75:8000/api/daya/reactive-power-t

GET http://192.168.101.75:8000/api/daya/apparent-power-r
GET http://192.168.101.75:8000/api/daya/apparent-power-s
GET http://192.168.101.75:8000/api/daya/apparent-power-t

GET http://192.168.101.75:8000/api/daya/total-active-power
```

Energi dan biaya:

```http
GET http://192.168.101.75:8000/api/daya/total-energi?periode=hari
GET http://192.168.101.75:8000/api/daya/total-energi?periode=bulan
GET http://192.168.101.75:8000/api/daya/total-energi?periode=tahun

GET http://192.168.101.75:8000/api/daya/estimasi?type=hari
GET http://192.168.101.75:8000/api/daya/estimasi?type=bulan
GET http://192.168.101.75:8000/api/daya/estimasi?type=tahun
```

---

# 2. URL chart untuk filter jam, hari, bulan, tahun

## Suhu & kelembapan

```http
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=suhu,kelembapan&filter=jam
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=suhu,kelembapan&filter=hari&bulan=5&tahun=2026
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=suhu,kelembapan&filter=bulan&tahun=2026
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=suhu,kelembapan&filter=tahun&tahun=2026
```

Custom tanggal dan jam:

```http
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=suhu,kelembapan&filter=hari&tanggal_mulai=2026-05-01&tanggal_selesai=2026-05-10&jam_mulai=08:00:00&jam_selesai=17:00:00
```

---

## PM2.5 & PM10

```http
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=pm25,pm10&filter=jam
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=pm25,pm10&filter=hari&bulan=5&tahun=2026
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=pm25,pm10&filter=bulan&tahun=2026
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=pm25,pm10&filter=tahun&tahun=2026
```

---

## VOC & eCO2

```http
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=tvoc,gas_co2&filter=jam
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=tvoc,gas_co2&filter=hari&bulan=5&tahun=2026
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=tvoc,gas_co2&filter=bulan&tahun=2026
GET http://192.168.101.75:8000/api/chart/monitoring?sensors=tvoc,gas_co2&filter=tahun&tahun=2026
```

---

## Tegangan

```http
GET http://192.168.101.75:8000/api/chart/listrik?sensors=voltage_l1l2,voltage_l2l3,voltage_l3l1&filter=jam
GET http://192.168.101.75:8000/api/chart/listrik?sensors=voltage_l1l2,voltage_l2l3,voltage_l3l1&filter=hari&bulan=5&tahun=2026
GET http://192.168.101.75:8000/api/chart/listrik?sensors=voltage_l1l2,voltage_l2l3,voltage_l3l1&filter=bulan&tahun=2026
GET http://192.168.101.75:8000/api/chart/listrik?sensors=voltage_l1l2,voltage_l2l3,voltage_l3l1&filter=tahun&tahun=2026
```

---

## Arus

```http
GET http://192.168.101.75:8000/api/chart/listrik?sensors=current_l1,current_l2,current_l3&filter=jam
GET http://192.168.101.75:8000/api/chart/listrik?sensors=current_l1,current_l2,current_l3&filter=hari&bulan=5&tahun=2026
GET http://192.168.101.75:8000/api/chart/listrik?sensors=current_l1,current_l2,current_l3&filter=bulan&tahun=2026
GET http://192.168.101.75:8000/api/chart/listrik?sensors=current_l1,current_l2,current_l3&filter=tahun&tahun=2026
```

---

## Daya aktif

```http
GET http://192.168.101.75:8000/api/chart/daya?sensors=active_power_r,active_power_s,active_power_t&filter=jam
GET http://192.168.101.75:8000/api/chart/daya?sensors=active_power_r,active_power_s,active_power_t&filter=hari&bulan=5&tahun=2026
GET http://192.168.101.75:8000/api/chart/daya?sensors=active_power_r,active_power_s,active_power_t&filter=bulan&tahun=2026
GET http://192.168.101.75:8000/api/chart/daya?sensors=active_power_r,active_power_s,active_power_t&filter=tahun&tahun=2026
```

Total daya aktif:

```http
GET http://192.168.101.75:8000/api/chart/daya?sensors=total_active_power&filter=jam
GET http://192.168.101.75:8000/api/chart/daya?sensors=total_active_power&filter=hari&bulan=5&tahun=2026
GET http://192.168.101.75:8000/api/chart/daya?sensors=total_active_power&filter=bulan&tahun=2026
GET http://192.168.101.75:8000/api/chart/daya?sensors=total_active_power&filter=tahun&tahun=2026
```

---

# 3. URL history untuk tabel bawah chart

## Monitoring history

```http
GET http://192.168.101.75:8000/api/monitoring/history
```

Per sensor:

```http
GET http://192.168.101.75:8000/api/monitoring/history?sensor=suhu&limit=10
GET http://192.168.101.75:8000/api/monitoring/history?sensor=kelembapan&limit=10
GET http://192.168.101.75:8000/api/monitoring/history?sensor=tvoc&limit=10
```

Custom tanggal:

```http
GET http://192.168.101.75:8000/api/monitoring/history?from=2026-05-01 00:00:00&to=2026-05-01 23:59:59&limit=100
```

---

## Listrik history

```http
GET http://192.168.101.75:8000/api/listrik/history
```

---

## Daya history

```http
GET http://192.168.101.75:8000/api/daya/history?limit=10
```

Per sensor:

```http
GET http://192.168.101.75:8000/api/daya/history?sensor=active_power_r&limit=10
GET http://192.168.101.75:8000/api/daya/history?sensor=total_active_power&limit=10
```

---

# 4. Cara pakai WebSocket di React

Install:

```bash
npm install laravel-echo pusher-js
```

Buat file:

```text
src/echo.js
```

Isi:

```javascript
import Echo from 'laravel-echo';
import Pusher from 'pusher-js';

window.Pusher = Pusher;

const echo = new Echo({
  broadcaster: 'reverb',
  key: 'efksfxfxxfrzuetobo5f',
  wsHost: '192.168.101.75',
  wsPort: 8080,
  wssPort: 8080,
  forceTLS: false,
  enabledTransports: ['ws'],
});

export default echo;
```

Contoh pakai untuk **chart suhu & kelembapan**:

```javascript
import echo from './echo';

echo.channel('monitoring.suhu')
  .listen('.suhu.updated', (event) => {
    console.log('Suhu realtime:', event);

    // tambahkan titik baru ke chart suhu
    // updateChart('suhu', event.value)
  });

echo.channel('monitoring.kelembapan')
  .listen('.kelembapan.updated', (event) => {
    console.log('Kelembapan realtime:', event);

    // tambahkan titik baru ke chart kelembapan
    // updateChart('kelembapan', event.value)
  });
```

Contoh pakai untuk **history realtime suhu & kelembapan**:

```javascript
echo.channel('monitoring.history')
  .listen('.monitoring.history.updated', (event) => {
    const newRow = {
      id: event.id,
      waktu: event.timestamp,
      suhu: event.suhu,
      kelembapan: event.kelembapan,
    };

    setTableData((prev) => [newRow, ...prev].slice(0, 10));
  });
```

---

# 5. Cara pakai WebSocket di Kotlin

Gunakan Pusher Java Client karena Laravel Reverb kompatibel dengan Pusher protocol.

Konfigurasi:

```kotlin
val options = PusherOptions()
options.setHost("192.168.101.75")
options.setWsPort(8080)
options.setWssPort(8080)
options.isUseTLS = false

val pusher = Pusher("efksfxfxxfrzuetobo5f", options)

pusher.connect()
```

Subscribe suhu:

```kotlin
val suhuChannel = pusher.subscribe("monitoring.suhu")

suhuChannel.bind("suhu.updated") { event ->
    Log.d("REALTIME_SUHU", event.data)
}
```

Subscribe history monitoring:

```kotlin
val historyChannel = pusher.subscribe("monitoring.history")

historyChannel.bind("monitoring.history.updated") { event ->
    Log.d("REALTIME_HISTORY", event.data)

    // parse JSON event.data
    // tambahkan row baru ke RecyclerView/Table
}
```

Subscribe biaya realtime:

```kotlin
val biayaChannel = pusher.subscribe("daya.biaya")

biayaChannel.bind("daya.biaya.updated") { event ->
    Log.d("REALTIME_BIAYA", event.data)
}
```

---

# 6. Daftar channel WebSocket yang harus dipakai

## Monitoring

| Kebutuhan          | Channel                 | Event                        |
| ------------------ | ----------------------- | ---------------------------- |
| Suhu               | `monitoring.suhu`       | `suhu.updated`               |
| Kelembapan         | `monitoring.kelembapan` | `kelembapan.updated`         |
| PM2.5              | `monitoring.pm25`       | `pm25.updated`               |
| PM10               | `monitoring.pm10`       | `pm10.updated`               |
| CO                 | `monitoring.gas_co`     | `gas_co.updated`             |
| CO2/eCO2           | `monitoring.gas_co2`    | `gas_co2.updated`            |
| TVOC/VOC           | `monitoring.tvoc`       | `tvoc.updated`               |
| Cahaya             | `monitoring.cahaya`     | `cahaya.updated`             |
| Kebisingan         | `monitoring.kebisingan` | `kebisingan.updated`         |
| IEQ                | `monitoring.ieq`        | `ieq.updated`                |
| History monitoring | `monitoring.history`    | `monitoring.history.updated` |

---

## Listrik

| Kebutuhan       | Channel                 | Event                     |
| --------------- | ----------------------- | ------------------------- |
| Voltage L1-L2   | `listrik.voltage.l1l2`  | `voltage_l1l2.updated`    |
| Voltage L2-L3   | `listrik.voltage.l2l3`  | `voltage_l2l3.updated`    |
| Voltage L3-L1   | `listrik.voltage.l3l1`  | `voltage_l3l1.updated`    |
| Current L1      | `listrik.current.l1`    | `current_l1.updated`      |
| Current L2      | `listrik.current.l2`    | `current_l2.updated`      |
| Current L3      | `listrik.current.l3`    | `current_l3.updated`      |
| Frequency       | `listrik.frecuency`     | `frecuency.updated`       |
| Power Factor    | `listrik.power_factor`  | `power_factor.updated`    |
| Total Voltage   | `listrik.total.voltage` | `total_voltage.updated`   |
| Total Current   | `listrik.total.current` | `total_current.updated`   |
| History listrik | `listrik.history`       | `listrik.history.updated` |

---

## Daya

| Kebutuhan        | Channel           | Event                      |
| ---------------- | ----------------- | -------------------------- |
| Active Power R   | `daya.active.r`   | `active_power_r.updated`   |
| Active Power S   | `daya.active.s`   | `active_power_s.updated`   |
| Active Power T   | `daya.active.t`   | `active_power_t.updated`   |
| Reactive Power R | `daya.reactive.r` | `reactive_power_r.updated` |
| Reactive Power S | `daya.reactive.s` | `reactive_power_s.updated` |
| Reactive Power T | `daya.reactive.t` | `reactive_power_t.updated` |
| Apparent Power R | `daya.apparent.r` | `apparent_power_r.updated` |
| Apparent Power S | `daya.apparent.s` | `apparent_power_s.updated` |
| Apparent Power T | `daya.apparent.t` | `apparent_power_t.updated` |
| Semua data daya  | `daya`            | `daya.updated`             |
| Biaya realtime   | `daya.biaya`      | `daya.biaya.updated`       |
| History daya     | `daya.history`    | `daya.history.updated`     |

---

# 7. Pola pemakaian di frontend

## Untuk card nilai terbaru

Saat halaman pertama dibuka:

```http
GET /monitoring/all
GET /listrik/all
GET /daya/all
```

Setelah itu listen WebSocket sesuai indikator.

Contoh:

```javascript
echo.channel('monitoring.suhu')
  .listen('.suhu.updated', (event) => {
    setSuhu(event.value);
  });
```

---

## Untuk chart realtime

Saat halaman pertama dibuka:

```http
GET /chart/monitoring?sensors=suhu,kelembapan&filter=jam
```

Setelah itu listen WebSocket sensor:

```javascript
echo.channel('monitoring.suhu')
  .listen('.suhu.updated', (event) => {
    addPointToChart('suhu', event.value);
  });

echo.channel('monitoring.kelembapan')
  .listen('.kelembapan.updated', (event) => {
    addPointToChart('kelembapan', event.value);
  });
```

---

## Untuk history realtime

Saat halaman pertama dibuka:

```http
GET /monitoring/history?limit=10
```

Setelah itu listen WebSocket history:

```javascript
echo.channel('monitoring.history')
  .listen('.monitoring.history.updated', (event) => {
    const newRow = {
      id: event.id,
      waktu: event.timestamp,
      suhu: event.suhu,
      kelembapan: event.kelembapan,
    };

    setTableData((prev) => [newRow, ...prev].slice(0, 10));
  });
```

---

# 8. Kesimpulan untuk frontend

Frontend harus memakai pola ini:

```text
REST API untuk data awal.
WebSocket untuk data baru realtime.
```

Yang memakai REST API:

```text
login
register
data awal card
data awal chart
data awal history
filter jam/hari/bulan/tahun
filter tanggal dan jam
admin user
assign teknisi
complete tugas teknisi
estimasi biaya total hari/bulan/tahun
```

Yang memakai WebSocket:

```text
card update realtime
chart bergerak realtime
history bertambah realtime
biaya realtime interval terbaru
IEQ realtime
notifikasi realtime jika dibutuhkan
```

Jadi chart dan history tetap perlu REST API untuk data awal, lalu WebSocket untuk update baru tanpa refresh.
