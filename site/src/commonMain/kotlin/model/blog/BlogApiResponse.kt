package model.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BlogApiResponse {
    @Serializable
    @SerialName("idle")
    data object Idle: BlogApiResponse()
    @Serializable
    @SerialName("success")
    data class Success(val data: List<Blog>): BlogApiResponse()
    @Serializable
    @SerialName("error")
    data class Error(val errorMessage: String): BlogApiResponse()
}

fun BlogApiResponse.parseAsString(): String {
    return when (this) {
        is BlogApiResponse.Idle -> {
            ""
        }

        is BlogApiResponse.Success -> {
            this.data.joinToString(separator = "\n") {
                it.content + " - ${it.postDate}"
            }
        }

        is BlogApiResponse.Error -> {
            this.errorMessage
        }
    }
}

fun BlogApiResponse.getTitle(): String {
    return when (this) {
        is BlogApiResponse.Idle -> {
            ""
        }

        is BlogApiResponse.Success -> {
            this.data.joinToString(separator ="\n"){
                it.title.toString().removeSurrounding("\"")
            }
        }

        is BlogApiResponse.Error -> {
            this.errorMessage
        }
    }
}