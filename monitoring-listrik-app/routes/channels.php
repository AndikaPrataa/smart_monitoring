<?php

use Illuminate\Support\Facades\Broadcast;

Broadcast::channel('App.Models.User.{id}', function ($user, $id) {
    return (int) $user->id === (int) $id;
});

/**
 * ============================================
 * MONITORING LINGKUNGAN (ENVIRONMENT) CHANNELS
 * ============================================
 */
Broadcast::channel('monitoring.suhu', function () {
    return true; // Public channel
});

Broadcast::channel('monitoring.kelembapan', function () {
    return true;
});

Broadcast::channel('monitoring.pm25', function () {
    return true;
});

Broadcast::channel('monitoring.pm10', function () {
    return true;
});

Broadcast::channel('monitoring.eco2', function () {
    return true;
});

Broadcast::channel('monitoring.tvoc', function () {
    return true;
});

Broadcast::channel('monitoring.lokasi', function () {
    return true;
});

/**
 * ============================================
 * LISTRIK (ELECTRICITY) CHANNELS
 * ============================================
 */
Broadcast::channel('listrik.voltage.l1l2', function () {
    return true;
});

Broadcast::channel('listrik.voltage.l2l3', function () {
    return true;
});

Broadcast::channel('listrik.voltage.l3l1', function () {
    return true;
});

Broadcast::channel('listrik.voltage.l1n', function () {
    return true;
});

Broadcast::channel('listrik.voltage.l2n', function () {
    return true;
});

Broadcast::channel('listrik.voltage.l3n', function () {
    return true;
});

Broadcast::channel('listrik.current.l1', function () {
    return true;
});

Broadcast::channel('listrik.current.l2', function () {
    return true;
});

Broadcast::channel('listrik.current.l3', function () {
    return true;
});

Broadcast::channel('listrik.current.n', function () {
    return true;
});

Broadcast::channel('listrik.frecuency', function () {
    return true;
});

Broadcast::channel('listrik.power_factor', function () {
    return true;
});

/**
 * ============================================
 * DAYA (POWER) CHANNELS
 * ============================================
 */
Broadcast::channel('daya.active.r', function () {
    return true;
});

Broadcast::channel('daya.active.s', function () {
    return true;
});

Broadcast::channel('daya.active.t', function () {
    return true;
});

Broadcast::channel('daya.reactive.r', function () {
    return true;
});

Broadcast::channel('daya.reactive.s', function () {
    return true;
});

Broadcast::channel('daya.reactive.t', function () {
    return true;
});

Broadcast::channel('daya.apparent.r', function () {
    return true;
});

Broadcast::channel('daya.apparent.s', function () {
    return true;
});

Broadcast::channel('daya.apparent.t', function () {
    return true;
});

