package me.camillebc.fictionhostapi

import java.io.File

data class Chapter(
    val id: String,
    var file: File? = null
) {
}