package api.blog

import model.blog.BlogApiResponse
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import db.Database
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

@Api("/getBlogs/{id}")
suspend fun getBlogById(ctx: ApiContext){
    try{
        val id = ctx.req.params.getValue("id")
        val blogs = ctx.data.getValue<Database>().getBlogs()
        val selectedBlog = blogs.filter {
            it.id == id
        }
        ctx.res.setBodyText(
            Json.encodeToString<BlogApiResponse>(
                value = BlogApiResponse.Success(data = selectedBlog)
            )
        )
    }catch(e: Exception){
        handleError(ctx, e)
    }
}

@Api("/getBlogs")
suspend fun getAllBlogs(ctx:ApiContext){
    try{
        val blogs = ctx.data.getValue<Database>().getBlogs()
        ctx.res.setBodyText(
            Json.encodeToString<BlogApiResponse>(
                value = BlogApiResponse.Success(data = blogs)
            )
        )
    }catch(e: Exception){
        handleError(ctx, e)
    }
}

fun handleError(ctx: ApiContext, e: Exception){
    ctx.res.setBodyText(
        Json.encodeToString(
            BlogApiResponse.Error(errorMessage = e.message.toString())
        )
    )
}