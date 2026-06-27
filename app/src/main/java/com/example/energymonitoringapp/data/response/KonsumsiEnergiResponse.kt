package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class KonsumsiEnergiResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("tahun") val tahun: Int? = null,
    @SerializedName("luas_bangunan_m2") val luasBangunanM2: Int? = null,
    @SerializedName("periode_pengamatan_hari") val periodePengamatanHari: Double? = null,
    @SerializedName("energy_awal_kwh") val energyAwalKwh: Double? = null,
    @SerializedName("energy_akhir_kwh") val energyAkhirKwh: Double? = null,
    @SerializedName("konsumsi_aktual_kwh") val konsumsiAktualKwh: Double? = null,
    @SerializedName("estimasi_tahunan_kwh") val estimasiTahunanKwh: Double? = null,
    @SerializedName("ike") val ike: Double? = null,
    @SerializedName("satuan") val satuan: String? = null,
    @SerializedName("kategori") val kategori: String? = null,
    @SerializedName("status_perhitungan") val statusPerhitungan: String? = null,
    @SerializedName("periode_data") val periodeData: PeriodeData? = null
)
