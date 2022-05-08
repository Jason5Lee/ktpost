package me.jason5lee.ktpost.deletePost

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.PostId
import me.jason5lee.ktpost.common.UserId
import me.jason5lee.ktpost.common.getNonNullLong
import me.jason5lee.resukt.Result

class Implementation(private val mysql: MySQLPool) : DeletePost {
  override suspend fun isCreator(user: UserId, post: PostId): Result<Boolean, Failure> {
    val row = mysql.preparedQuery("SELECT creator FROM posts WHERE post_id=?")
      .execute(Tuple.of(post.value))
      .await()
      .firstOrNull() ?: return Result.failure(Failure.PostNotFound)
    return Result.success(row.getNonNullLong("creator") == user.value)
  }

  override suspend fun deletePost(post: PostId): Result<Unit, Failure> =
    mysql
      .preparedQuery("DELETE FROM posts WHERE post_id=?")
      .execute(Tuple.of(post.value))
      .await()
      .rowCount()
      .let {
        if (it == 1) Result.success(Unit) else Result.failure(Failure.PostNotFound)
      }
}
