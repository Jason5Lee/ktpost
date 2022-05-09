package me.jason5lee.ktpost.editPost

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result

class Implementation(private val mysql: MySQLPool) : EditPost {
  override suspend fun checkCreatorAndPostType(
    post: PostId,
    expectedCreator: UserId,
    content: PostContent
  ): Result<Unit, Failure> {
    val postRow = mysql.preparedQuery("SELECT creator,post,url FROM posts WHERE post_id=?")
      .execute(Tuple.of(post.value))
      .await()
      .firstOrNull() ?: return Result.failure(Failure.PostNotFound)
    val creator = postRow.getNonNullLong("creator")
    if (creator != expectedCreator.value) {
      return Result.failure(Failure.NotCreator)
    }

    val contentMatch = when (content) {
      is PostContent.Post -> postRow.getString("post") != null
      is PostContent.Url -> postRow.getString("url") != null
    }
    return if (!contentMatch) {
      Result.failure(Failure.PostTypeDiffers)
    } else {
      Result.success(Unit)
    }
  }

  override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Failure> {
    when (newContent) {
      is PostContent.Post -> {
        mysql.preparedQuery("UPDATE posts SET post=? WHERE post_id=?")
          .execute(Tuple.of(newContent.text, post.value))
      }
      is PostContent.Url -> {
        mysql.preparedQuery("UPDATE posts SET url=? WHERE post_id=?")
          .execute(Tuple.of(newContent.url.toString(), post.value))
      }
    }.await()
    return Result.success(Unit)
  }
}
