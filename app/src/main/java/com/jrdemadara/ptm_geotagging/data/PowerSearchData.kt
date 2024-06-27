package com.jrdemadara.ptm_geotagging.data

data class PowerSearchData(
    val id: String,
    val precinct: String,
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val extension: String,
    val birthdate: String,
    val occupation: String,
    val phone: String,
    val qrcode: String,
    val hasptmid: Int,
    val isUploaded: Int,

)
