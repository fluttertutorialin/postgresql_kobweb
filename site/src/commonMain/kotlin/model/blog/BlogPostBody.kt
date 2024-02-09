package model.blog

import kotlinx.serialization.Serializable

@Serializable
data class BlogPostBody(
    val title: String,
    val content: String,
    val postDate: String = ""
)

