package com.ndproject.data.model

import com.google.gson.annotations.SerializedName

data class CryptoDetailsResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: Description,
    @SerializedName("categories")
    val categories: List<String>,
    @SerializedName("image")
    val image: Image
)

data class Description(
    @SerializedName("ru")
    val ru: String
)

data class Image(
    @SerializedName("large")
    val large: String
)