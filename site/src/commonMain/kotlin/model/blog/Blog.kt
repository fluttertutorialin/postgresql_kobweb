package model.blog

import kotlinx.serialization.Serializable

@Serializable
data class Blog(
    val content: String? = null,
    val id: String? = null,
    val postDate: String? = null,
    val title: String? = null
)
