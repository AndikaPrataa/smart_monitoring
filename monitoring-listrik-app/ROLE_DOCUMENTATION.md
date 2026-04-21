# 🔐 API Authentication dengan Roles Documentation

## 📋 Overview

API sekarang mendukung 2 peran (roles):
- **Admin** - Akses penuh ke semua endpoint dan fitur manajemen
- **Teknisi** - Akses untuk membaca dan mengupdate data sensor (default role)

---

## 👥 Available Roles

### 1. **ADMIN**
- Akses penuh ke semua endpoint
- Dapat membuat user baru sebagai admin atau teknisi
- Dapat mengelola sistem
- Full read & write access ke semua data

### 2. **TEKNISI**
- Akses read ke semua data sensor
- Dapat update status dan data monitoring
- Default role untuk user baru jika tidak dispesifikasi

---

## 🔗 Authentication Endpoints

### **1. REGISTER - Daftar User Baru**

**Endpoint:**
```
POST /api/auth/register
```

**Body (dengan role):**
```json
{
  "name": "Nama User",
  "email": "email@example.com",
  "password": "password123",
  "password_confirmation": "password123",
  "role": "admin"
}
```

**Body (tanpa role - default teknisi):**
```json
{
  "name": "Nama User",
  "email": "email@example.com",
  "password": "password123",
  "password_confirmation": "password123"
}
```

**Valid Roles:**
- `admin`
- `teknisi` (default)

**Response (201):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "Nama User",
    "email": "email@example.com",
    "role": "admin",
    "token": "7|randomtoken...",
    "token_type": "Bearer"
  }
}
```

---

### **2. LOGIN - Login User**

**Endpoint:**
```
POST /api/auth/login
```

**Body:**
```json
{
  "email": "email@example.com",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": 1,
    "name": "Nama User",
    "email": "email@example.com",
    "role": "admin",
    "token": "7|randomtoken...",
    "token_type": "Bearer"
  }
}
```

---

### **3. GET PROFILE - Ambil Data User**

**Endpoint:**
```
GET /api/auth/me
Headers: Authorization: Bearer {token}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Nama User",
    "email": "email@example.com",
    "role": "admin",
    "created_at": "2026-04-20T10:00:00Z"
  }
}
```

---

### **4. LOGOUT - Logout User**

**Endpoint:**
```
POST /api/auth/logout
Headers: Authorization: Bearer {token}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

---

## 📊 Sample Users untuk Testing

| Email | Password | Role | Status |
|-------|----------|------|--------|
| admin@example.com | admin1234 | admin | ✅ Seeded |
| teknisi@example.com | teknisi1234 | teknisi | ✅ Seeded |
| alyaayra@gmail.com | alya1234 | admin | ✅ Seeded |

---

## 🧪 Testing di Postman

### **Test 1: Register as ADMIN**

1. POST `/api/auth/register`
2. Body:
```json
{
  "name": "New Admin",
  "email": "newadmin@example.com",
  "password": "pass1234",
  "password_confirmation": "pass1234",
  "role": "admin"
}
```

### **Test 2: Register as TEKNISI (Default)**

1. POST `/api/auth/register`
2. Body:
```json
{
  "name": "New Teknisi",
  "email": "newteknisi@example.com",
  "password": "pass1234",
  "password_confirmation": "pass1234"
}
```
(role akan otomatis jadi "teknisi")

### **Test 3: Login & Get Token**

1. POST `/api/auth/login`
2. Body:
```json
{
  "email": "admin@example.com",
  "password": "admin1234"
}
```
3. Copy token dari response

### **Test 4: Get Profile dengan Token**

1. GET `/api/auth/me`
2. Headers:
   - Authorization: `Bearer {token yang sudah di-copy}`
3. Lihat role di response

### **Test 5: Logout**

1. POST `/api/auth/logout`
2. Headers:
   - Authorization: `Bearer {token yang sama}`
3. Token sudah di-revoke dan tidak bisa digunakan lagi

---

## 🔐 Role-Based Access Control (RBAC)

### **Middleware untuk Protect Routes**

Untuk melindungi endpoint berdasarkan role:

```php
// Admin only
Route::middleware('auth:sanctum', 'role:admin')->group(function () {
    // Admin endpoints
});

// Teknisi only
Route::middleware('auth:sanctum', 'role:teknisi')->group(function () {
    // Teknisi endpoints
});
```

---

## 📝 Database Schema

```sql
-- Users table dengan role column
CREATE TABLE users (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'teknisi') DEFAULT 'teknisi',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Personal access tokens (Sanctum)
CREATE TABLE personal_access_tokens (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    tokenable_type VARCHAR(255) NOT NULL,
    tokenable_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
    token VARCHAR(80) UNIQUE NOT NULL,
    abilities TEXT,
    last_used_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## ✅ Checklist Testing

- [ ] Register user dengan role admin
- [ ] Register user dengan role teknisi (default)
- [ ] Register user tanpa role field (auto teknisi)
- [ ] Login dan dapatkan token
- [ ] GET /api/auth/me dengan token (lihat role)
- [ ] Logout dan revoke token
- [ ] Coba akses endpoint dengan token yang sudah logout (error 401)

---

## 🚀 Next Steps

1. **Implement Role-based Sensor Access:**
   - Admin: Full access ke semua sensor
   - Teknisi: Read-only ke semua sensor, write ke status

2. **Add Role-Protected Endpoints:**
   ```
   PUT /api/admin/users/{id} - Update user role
   DELETE /api/admin/users/{id} - Delete user
   GET /api/admin/users - List all users (admin only)
   ```

3. **Add Activity Logging:**
   - Log setiap action berdasarkan user role

---

**Last Updated:** 2026-04-20  
**Status:** ✅ Production Ready
