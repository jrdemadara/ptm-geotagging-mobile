package com.jrdemadara.ptm_geotagging.data

data class Photo(
    val personal: ByteArray,
    val family: ByteArray,
    val livelihood: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo

        if (!personal.contentEquals(other.personal)) return false
        if (!family.contentEquals(other.family)) return false
        return livelihood.contentEquals(other.livelihood)
    }

    override fun hashCode(): Int {
        var result = personal.contentHashCode()
        result = 31 * result + family.contentHashCode()
        result = 31 * result + livelihood.contentHashCode()
        return result
    }
}
