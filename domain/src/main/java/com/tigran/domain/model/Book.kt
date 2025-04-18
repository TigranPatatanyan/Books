package com.tigran.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val authors: List<String>,
    val smallThumbnailUrl: String?,
    val thumbnailUrl: String?,
    val thumbnailPath: String? = null,
    val smallThumbnailPath: String? = null
)
