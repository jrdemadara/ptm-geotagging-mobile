package com.jrdemadara.ptm_geotagging.util

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun ByteArray.toMultipartBodyPart(name: String, fileName: String): MultipartBody.Part {
    val requestBody = this.toRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, fileName, requestBody)
}