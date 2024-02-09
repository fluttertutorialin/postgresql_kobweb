package model.todo

import kotlinx.serialization.Serializable

@Serializable
class TodoItem(
    val id: String,
    val text: String,
)