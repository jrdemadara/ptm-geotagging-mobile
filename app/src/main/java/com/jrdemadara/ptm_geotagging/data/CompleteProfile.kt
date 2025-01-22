package com.jrdemadara.ptm_geotagging.data

data class ProfileV(
    val id: Int,
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val extension: String,
    val precinct: String,
    val barangay: String,
    val purok: String,
    val phone: String,
    val solo: String,
    val family: String,
    val household: String,
    val lat: String,
    val lon: String,
    val status: String
)

data class LivelihoodV(val livelihood: String, val description: String)
data class BeneficiaryV(val precinct: String, val fullname: String, val birthdate: String)
data class SkillV(val skill: String)
data class AssistanceV(val assistance: String, val amount: Double, val released_at: String)

data class ProfileResponse(
    val profile: ProfileV,
    val livelihood: List<LivelihoodV>,
    val beneficiary: List<BeneficiaryV>,
    val skill: List<SkillV>,
    val assistance: List<AssistanceV>
)