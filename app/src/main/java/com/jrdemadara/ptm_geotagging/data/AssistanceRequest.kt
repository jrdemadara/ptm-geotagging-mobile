package com.jrdemadara.ptm_geotagging.data

data class AssistanceRequest(
    val profile_id: Int,
    val assistance: String,
    val amount: Double,
    val released_at: String
)
