package com.jrdemadara.ptm_geotagging.data

data class ProfileWithDetails(
    val profile: Profile,
    val beneficiaries: List<Beneficiary>,
    val skills: List<String>,
    val livelihoods: List<String>,
    val photo: Photo
)
