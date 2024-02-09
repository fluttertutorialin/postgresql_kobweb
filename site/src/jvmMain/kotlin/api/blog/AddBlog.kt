package api.blog

import model.blog.BlogPostBody
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.readBodyText
import db.Database
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.time.LocalDate

@Api("/addBlog")
fun addBlog(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return

    val blogContent = ctx.req.readBodyText()?.let { Json.parseToJsonElement(it) }

    blogContent?.let { content ->
        ctx.data.getValue<Database>().addBlog(
            BlogPostBody(
                content = content.jsonObject.getValue("content").toString(),
                postDate = LocalDate.now().toString(),
                title = content.jsonObject.getValue("title").toString()
            )
        )
    }

    ctx.res.status = 200
}