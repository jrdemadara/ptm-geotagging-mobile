package com.jrdemadara.ptm_geotagging.data

data class SearchMembers(
    val qrcode: String,
    val precinct: String,
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val extension: String,
    val birthdate: String,
    val phone: String,
    val occupation: String,
    val purok: String,
    val is_muslim: Boolean,
    val has_ptmid: String,
)
