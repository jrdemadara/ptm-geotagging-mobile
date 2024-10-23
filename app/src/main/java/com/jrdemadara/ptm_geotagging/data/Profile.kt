package com.jrdemadara.ptm_geotagging.data

data class Profile(
    val id: String,
    val precinct: String,
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val extension: String,
    val birthdate: String,
    val occupation: String,
    val phone: String,
    val lat: String,
    val lon: String,
    val barangay: String,
    val purok: String,
    val qrcode: String,
    val hasptmid: Int,
    val ismuslim: Int,
)
