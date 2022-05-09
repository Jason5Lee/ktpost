package me.jason5lee.ktpost.listPosts

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*

class Implementation(private val logger: mu.KLogger, private val mysql: MySQLPool) : ListPosts {
  override suspend fun invoke(query: Query): Output {
    val postRows = mysql.preparedQuery("SELECT * FROM posts ORDER BY creation_time DESC LIMIT ?, ?")
      .execute(Tuple.of(query.offset.value, query.size.value))
      .await()

    val userId2Name = mysql.query(
      "SELECT user_id, user_name FROM users WHERE user_id IN (${
        postRows.asSequence().map { it.getNonNullLong("creator") }.joinToString(",")
      })"
    )
      .execute()
      .await()
      .associate { row -> row.getNonNullLong("user_id") to row.getStringModel("user_name") { UserName.new(it) } }

    return Output(
      posts = postRows.map { row ->
        PostInfo(
          id = PostId(row.getNonNullLong("post_id")),
          title = row.getStringModel("title") { PostTitle.new(it) },
          creator = Creator(
            id = UserId(row.getNonNullLong("creator")),
            name = userId2Name[row.getNonNullLong("creator")] ?: row.throwInvalidInDB(
              "creator",
              "creator not found in users"
            )
          ),
          creation = Time(utc = row.getNonNullLong("creation_time")),
        )
      },
    )
  }
}
