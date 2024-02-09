package model.blog

import kotlinx.serialization.Serializable

@Serializable
data class BlogPutBody(
    val id: String,
    val newTitle: String,
    val newContent: String
)