# 🚀 API QUICK REFERENCE CARD

**34 GET Endpoints • 28 WebSocket Channels • 100% Complete**

---

## 📊 MONITORING LINGKUNGAN (7 endpoints)

| Endpoint | Channel | Unit | Description |
|----------|---------|------|-------------|
| `/api/monitoring/suhu` | `monitoring.suhu` | °C | Temperature |
| `/api/monitoring/kelembapan` | `monitoring.kelembapan` | % | Humidity |
| `/api/monitoring/pm25` | `monitoring.pm25` | µg/m³ | PM2.5 |
| `/api/monitoring/pm10` | `monitoring.pm10` | µg/m³ | PM10 |
| `/api/monitoring/eco2` | `monitoring.eco2` | ppm | eCO2 |
| `/api/monitoring/tvoc` | `monitoring.tvoc` | ppb | TVOC |
| `/api/monitoring/lokasi` | `monitoring.lokasi` | text | Location |
| `/api/monitoring/all` | - | - | All data combined |
| `/api/monitoring/history` | - | - | Last 100 records |

---

## ⚡ LISTRIK (12 endpoints)

### Voltage (6 endpoints)
| Endpoint | Channel | Unit |
|----------|---------|------|
| `/api/listrik/voltage-l1l2` | `listrik.voltage.l1l2` | V |
| `/api/listrik/voltage-l2l3` | `listrik.voltage.l2l3` | V |
| `/api/listrik/voltage-l3l1` | `listrik.voltage.l3l1` | V |
| `/api/listrik/voltage-l1n` | `listrik.voltage.l1n` | V |
| `/api/listrik/voltage-l2n` | `listrik.voltage.l2n` | V |
| `/api/listrik/voltage-l3n` | `listrik.voltage.l3n` | V |

### Current (4 endpoints)
| Endpoint | Channel | Unit |
|----------|---------|------|
| `/api/listrik/current-l1` | `listrik.current.l1` | A |
| `/api/listrik/current-l2` | `listrik.current.l2` | A |
| `/api/listrik/current-l3` | `listrik.current.l3` | A |
| `/api/listrik/current-n` | `listrik.current.n` | A |

### System (2 endpoints)
| Endpoint | Channel | Unit |
|----------|---------|------|
| `/api/listrik/frecuency` | `listrik.frecuency` | Hz |
| `/api/listrik/power-factor` | `listrik.power_factor` | - |

### Combined (2 endpoints)
| Endpoint | Description |
|----------|-------------|
| `/api/listrik/all` | All data combined |
| `/api/listrik/history` | Last 100 records |

---

## 🔋 DAYA (9 endpoints)

### Active Power (3 endpoints)
| Endpoint | Channel | Unit |
|----------|---------|------|
| `/api/daya/active-power-r` | `daya.active.r` | W |
| `/api/daya/active-power-s` | `daya.active.s` | W |
| `/api/daya/active-power-t` | `daya.active.t` | W |

### Reactive Power (3 endpoints)
| Endpoint | Channel | Unit |
|----------|---------|------|
| `/api/daya/reactive-power-r` | `daya.reactive.r` | VAR |
| `/api/daya/reactive-power-s` | `daya.reactive.s` | VAR |
| `/api/daya/reactive-power-t` | `daya.reactive.t` | VAR |

### Apparent Power (3 endpoints)
| Endpoint | Channel | Unit |
|----------|---------|------|
| `/api/daya/apparent-power-r` | `daya.apparent.r` | VA |
| `/api/daya/apparent-power-s` | `daya.apparent.s` | VA |
| `/api/daya/apparent-power-t` | `daya.apparent.t` | VA |

### Combined (2 endpoints)
| Endpoint | Description |
|----------|-------------|
| `/api/daya/all` | All data combined |
| `/api/daya/history` | Last 100 records |

---

## 🔌 WEBSOCKET EXAMPLES

### Listen Suhu (JavaScript)
```javascript
Echo.channel('monitoring.suhu')
    .listen('suhu.updated', (event) => {
        console.log('Temperature:', event.value, '°C');
    });
```

### Listen Voltage
```javascript
Echo.channel('listrik.voltage.l1l2')
    .listen('voltage_l1l2.updated', (event) => {
        console.log('Voltage L1-L2:', event.value, 'V');
    });
```

### Listen Power
```javascript
Echo.channel('daya.active.r')
    .listen('active_power_r.updated', (event) => {
        console.log('Active Power R:', event.value, 'W');
    });
```

---

## 📱 CURL EXAMPLES

```bash
# Get latest temperature
curl http://localhost:8000/api/monitoring/suhu

# Get all monitoring
curl http://localhost:8000/api/monitoring/all

# Get voltage history
curl http://localhost:8000/api/listrik/history

# Get all power data
curl http://localhost:8000/api/daya/all

# Get API docs
curl http://localhost:8000/api/docs
```

---

## 📋 UTILITY ENDPOINTS

| Endpoint | Response |
|----------|----------|
| `GET /api/docs` | Full API documentation |
| `GET /api/channels` | List of all broadcast channels |
| `GET /api/user` | Current user (auth required) |

---

## ✅ COMPLETE CHECKLIST

- ✅ 34 GET endpoints (1 per database column)
- ✅ 28 WebSocket broadcast channels
- ✅ 3 Controllers (9 + 14 + 11 methods)
- ✅ 28 Event classes
- ✅ Real-time broadcasting
- ✅ Historical data endpoints
- ✅ Combined data endpoints
- ✅ API documentation
- ✅ Channel list endpoint

---

## 🚀 QUICK START

### 1. Import Database
```bash
mysql -u root < monitoring_listrik.sql
```

### 2. Start Servers
```bash
# Terminal 1: Reverb WebSocket
php artisan reverb:start

# Terminal 2: Laravel Dev
php artisan serve

# Terminal 3: Vite Frontend
npm run dev
```

### 3. Test Endpoint
```bash
curl http://localhost:8000/api/monitoring/suhu
```

### 4. View Real-time Updates
```javascript
Echo.channel('monitoring.suhu')
    .listen('suhu.updated', (e) => console.log(e.value));
```

---

**Status**: ✅ PRODUCTION READY  
**Base URL**: http://localhost:8000/api  
**Total Endpoints**: 34 + 3 utility = 37  
**WebSocket**: ws://127.0.0.1:8080

