package com.jrdemadara.ptm_geotagging.util

    fun String.capitalizeWords(delimiter: String = " ") =
        split(delimiter).joinToString(delimiter) { word ->

            val smallCaseWord = word.lowercase()
            smallCaseWord.replaceFirstChar(Char::titlecaseChar)

        }