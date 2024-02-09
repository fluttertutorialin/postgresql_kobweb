package db

import model.blog.Blog
import model.blog.BlogPostBody
import model.blog.BlogPutBody
import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
import java.sql.Connection
import java.sql.SQLException
import java.util.*

class Database {
    private val hikariConfig = HikariConfig()

    private val connectionPool = buildConnectionPool()
    private fun buildConnectionPool(): Connection{
        hikariConfig.jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
        hikariConfig.username = "postgres"
        hikariConfig.password = "admin"
        hikariConfig.setDriverClassName(Driver::class.java.name)

        val dataSource = HikariDataSource(hikariConfig)
        return dataSource.connection
    }

    fun updateBlog(blogPutBody: BlogPutBody) {
        val connect = connectionPool
        try {
            val insertSQL = "UPDATE Blogs" +
                    " SET content=?, title=? " +
                    " WHERE id=? "
            val prepareStatement = connect.prepareStatement(insertSQL)

            prepareStatement?.setString(1, blogPutBody.newContent)
            prepareStatement?.setString(2, blogPutBody.newTitle)
            prepareStatement?.setString(3, blogPutBody.id)

            prepareStatement?.executeUpdate()

            prepareStatement?.close()
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    fun getBlogs(): List<Blog> {
        val connect = connectionPool

        val mutableList = mutableListOf<Blog>()
        try {
            val st = connect.createStatement()
            val rs = st?.executeQuery("SELECT * FROM blogs")


            while (rs?.next() == true) {
                mutableList.add(
                    Blog(
                        content = rs.getString("content"),
                        id = rs.getString("id"),
                        postDate = rs.getString("postdate"),
                        title = rs.getString("title")
                    )
                )
            }

            st?.close()
            rs?.close()
        } catch (e: SQLException) {
            println(e.message)
        }
        return mutableList
    }

    fun addBlog(blogPostBody: BlogPostBody) {
        val connect = connectionPool
        try {
            val insertSQL = "INSERT INTO Blogs" +
                    " (content, id, postdate, title) VALUES " +
                    " (?, ?, ?, ?);"
            val prepareStatement = connect.prepareStatement(insertSQL)

            prepareStatement?.setString(1, blogPostBody.content)
            prepareStatement?.setString(2, UUID.randomUUID().toString())
            prepareStatement?.setString(3, blogPostBody.postDate)
            prepareStatement?.setString(4, blogPostBody.title)

            prepareStatement?.executeUpdate()

            prepareStatement?.close()
        } catch (e: SQLException) {
            println(e.message)
        }
    }
}

@InitApi
fun initDatabase(ctx: InitApiContext) {
    ctx.data.add(Database())
}



