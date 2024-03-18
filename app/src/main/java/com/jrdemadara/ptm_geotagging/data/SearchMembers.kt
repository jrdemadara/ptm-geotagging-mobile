package com.jrdemadara.ptm_geotagging.data

data class SearchMembers(
    val precinct: String,
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val extension: String,
    val birthdate: String,
    val contact: String,
    val occupation: String,
    val isptmid: Int,
)
