package me.jason5lee.ktpost.createPost

import com.relops.snowflake.Snowflake
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result
import java.time.Instant

class Implementation(val mysql: MySQLPool, val snowflake: Snowflake) : CreatePost {
  override suspend fun invoke(command: Command): Result<PostId, Failure> {
    val id = snowflake.next()
    val url: String?
    val post: String?
    when (command.content) {
      is PostContent.Post -> {
        url = null
        post = command.content.text
      }
      is PostContent.Url -> {
        url = command.content.url.toString()
        post = null
      }
    }

    try {
      mysql.preparedQuery(
        "INSERT INTO posts (post_id, creator, creation_time, title, url, post) " +
          "VALUES (?,?,?,?,?,?)"
      )
        .execute(Tuple.of(id, command.creator.value, Instant.now().toEpochMilli(), command.title.value, url, post))
        .await()
    } catch (e: Exception) {
      e.message?.let { msg ->
        if (msg.contains("Duplicate entry") && msg.contains("for key 'UC_title'")) {
          return Result.failure(Failure.DuplicatedTitle)
        }
      }
    }

    return Result.success(PostId(id))
  }
}
