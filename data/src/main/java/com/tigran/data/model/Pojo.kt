package com.tigran.data.model

import com.google.gson.annotations.SerializedName

data class BookResponse(
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("items") val items: List<BookItem>?
)

data class BookItem(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("title") val title: String,
    @SerializedName("publishedDate") val publishedDate: String?,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?,
    @SerializedName("authors") val authors: List<String>?
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("smallThumbnail") val smallThumbnail: String?
)
