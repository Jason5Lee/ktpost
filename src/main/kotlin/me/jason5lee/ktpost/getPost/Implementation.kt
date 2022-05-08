package me.jason5lee.ktpost.getPost

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.*

class Implementation(val mysql: MySQLPool) : GetPost {
  override suspend fun invoke(input: Query): Result<PostInfoForPage, Failure> {
    val postRow = mysql.preparedQuery("SELECT * FROM posts WHERE post_id=?")
      .execute(Tuple.of(input.value))
      .await()
      .firstOrNull() ?: return Result.failure(Failure.PostNotFound)

    val creatorId = UserId(postRow.getNonNullLong("creator"))
    val creationTime = Time(postRow.getNonNullLong("creation_time"))
    val lastModified = postRow.getLong("last_modified")?.let { Time(it) }
    val title = postRow.getStringModel("title") { PostTitle.new(it) }
    val postContent =
      postRow.getString("post")?.let { PostContent.Post(it) }
        ?: postRow.getStringModel("url") { PostContent.Url.create(it) }

    val userRow = mysql.preparedQuery("SELECT user_name FROM users WHERE user_id=?")
      .execute(Tuple.of(creatorId.value))
      .await()
      .firstOrNull() ?: throw Exception("creator of the post `${input.value}` not found in users")
    val creatorName = userRow.getStringModel("user_name") { UserName.new(it) }

    return Result.success(
        PostInfoForPage(
      creator = CreatorInfo(name = creatorName, id = creatorId),
      creation = creationTime,
      lastModified = lastModified,
      title = title,
      content = postContent,
    )
    )
  }
}
