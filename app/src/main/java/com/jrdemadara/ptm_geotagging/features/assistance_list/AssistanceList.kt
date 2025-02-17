package com.jrdemadara.ptm_geotagging.features.assistance_list

data class AssistanceList(
    val date: String,
    val assistance: String,
    val fullname: String,
    val barangay: String,
    val purok: String,
    val amount: String,
    val endorser: String,
)
