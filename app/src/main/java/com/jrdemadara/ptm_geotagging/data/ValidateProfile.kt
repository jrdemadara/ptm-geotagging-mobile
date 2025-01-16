package com.jrdemadara.ptm_geotagging.data

data class ValidateProfile(
    var id: Int = 0,
    var lastname: String? = null,
    var firstname: String? = null,
    var middlename: String? = null,
    var extension: String? = null,
    var precinct: String? = null,
    var barangay: String? = null,
    var purok: String? = null,
    var phone: String? = null,
    var image: String? = null,
    var assistanceExists: Boolean = false
)