package todo.pages

import model.blog.Blog
import model.blog.BlogApiResponse
import model.blog.BlogPostBody
import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.px
import org.w3c.dom.HTMLInputElement
import model.todo.ApiResponse
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.css.Resize
import com.varabyte.kobweb.compose.css.resize
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.cols
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.rows
import org.jetbrains.compose.web.dom.*
import model.todo.parseResponseAsString

@Page
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    var apiResponseTextState by remember { mutableStateOf("") }

    var blogObjectState by remember { mutableStateOf(listOf<Blog>()) }
    LaunchedEffect(Unit) {
        blogObjectState = (fetchBlogs() as BlogApiResponse.Success).data
    }

    var bodyText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().margin(top = 20.px)
            .backgroundColor(com.varabyte.kobweb.compose.ui.graphics.Color.rgb(200, 200, 200)).padding(all = 20.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        P{
            blogObjectState.forEach {
                Text(
                   it.title.toString().removeSurrounding("\""),
                )
                Br()
            }
        }

        Column {
            Input(
                type = InputType.Text,
                attrs = Modifier.toAttrs(finalHandler = {
                    this.onChange {
                        titleText = it.value
                    }
                    this.placeholder("Enter blog title here")
                })
            )

            TextArea(
                attrs = Modifier.toAttrs(finalHandler = {
                    this.rows(5)
                    this.cols(40)
                    this.style {
                        this.resize(Resize.None)
                    }
                    this.onChange {
                        bodyText = it.value
                    }
                    this.placeholder("Write your thoughts here")
                })
            )

            Button(attrs = Modifier.onClick {
                scope.launch {
                    if (bodyText.isNotEmpty() && titleText.isNotEmpty()) {
                        postBlog(bodyText, titleText)
                    }
                }
            }.toAttrs()) {
                SpanText("make post")
            }
        }

        SpanText(
            modifier = Modifier
                .margin(bottom = 8.px)
                .fontFamily("Roboto")
                .fontSize(18.px)
                .color(Color.white)
                .fontWeight(FontWeight.Medium), text = "How many people?"
        )
        Input(
            type = InputType.Number, attrs = Modifier.margin(bottom = 16.px)
                .fontFamily("Roboto")
                .fontSize(14.px)
                .id("countInput")
                .toAttrs {
                    attr("placeholder", "Enter number")

                }
        )
        Button(
            attrs = Modifier
                .margin(bottom = 16.px)
                .fontFamily("Roboto")
                .fontSize(16.px)
                .backgroundColor(Color.lightblue)
                .onClick {
                    scope.launch {
                        val apiResponse = fetchPeople()
                        apiResponseTextState = apiResponse.parseResponseAsString()
                    }
                }.toAttrs()
        )
        {
            SpanText("Get people")
        }

        P(
            attrs = Modifier
                .fontFamily("Roboto").fontSize(16.px).toAttrs()
        ) {
            apiResponseTextState.split("\n").forEach {
                Text(value = it)
                Br()
            }
        }
    }


}


private suspend fun fetchPeople(): ApiResponse {
    return try {

        val userInput = (document.getElementById("countInput") as HTMLInputElement).value
        val number = if (userInput.isEmpty()) 0 else userInput.toInt()

        val result = window.api.tryGet(apiPath = "getPeople?peopleCount=$number")?.decodeToString()
        println("RESULT FROM API $result")
        Json.decodeFromString(result.toString())

    } catch (e: Exception) {
        return ApiResponse.Error(errorMessage = e.message.toString())
    }
}

private suspend fun fetchBlogs(): BlogApiResponse {
    val result = window.api.tryGet(apiPath = "getBlogs")?.decodeToString()
    return Json.decodeFromString(string = result.toString())
}

private suspend fun postBlog(
    content: String,
    title: String,
): BlogApiResponse {
    val blogPostBodyByteArray = Json.encodeToString(BlogPostBody(content = content, title = title)).encodeToByteArray()
    val results = window.api.tryPost(apiPath = "addBlog", body = blogPostBodyByteArray)?.decodeToString()

    return Json.decodeFromString(string = results.toString())
}