package me.jason5lee.ktpost.listPostsByCreator

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.*

class Implementation(private val mysql: MySQLPool) : ListPostByCreator {
  override suspend fun invoke(input: Query): Result<List<PostInfo>, Failure> {
    mysql.preparedQuery("SELECT 0 FROM users WHERE user_id=?")
      .execute(Tuple.of(input.creator.value))
      .await()
      .firstOrNull() ?: return Result.failure(Failure.CreatorNotFound)

    val posts = mysql.preparedQuery("SELECT * FROM posts WHERE creator=? LIMIT ?,?")
      .execute(Tuple.of(input.creator.value, input.offset.value, input.size.value))
      .await()
      .map { row ->
        PostInfo(
          PostId(row.getNonNullLong("post_id")),
          row.getStringModel("title") { PostTitle.new(it) },
          Time(utc = row.getNonNullLong("creation_time"))
        )
      }

    return Result.success(posts)
  }
}
