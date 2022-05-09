package me.jason5lee.ktpost.listPostsByCreator

import io.vertx.ext.web.RoutingContext
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.andThen
import me.jason5lee.resukt.getOrElse

internal fun listPostsByCreatorApi(listPostByCreator: ListPostByCreator) = api(
  path = { get("/user/:userId/posts") },
) { ctx ->
  val query = Query(
    creator = UserId(
      parseId(ctx.pathParam("userId") ?: return@api ctx.respondNoUserId())
        .getOrElse { return@api ctx.respondUserNotFound() }
    ),
    offset = ctx.getQueryParamOptionalInt("offset")
      .andThen { Offset.new(it) }
      .getOrElse { return@api ctx.respondJson(400, it) },
    size = ctx.getQueryParamInt("size")
      .andThen { Size.new(it) }
      .getOrElse { return@api ctx.respondJson(400, it) },
  )

  val posts = listPostByCreator(query).getOrElse {
    return@api when (it) {
      Failure.CreatorNotFound -> ctx.respondUserNotFound()
    }
  }

  @Suppress("unused")
  class PostInfoDto(
    val id: String,
    val title: String,
    val creationTime: Long,
  )

  ctx.json(posts.map { PostInfoDto(formatId(it.id.value), it.title.value, it.creation.utc) })
}

private suspend fun RoutingContext.respondNoUserId() =
  respondJson(400, FailureBody(field = "userId", reason = "no userId"))

private suspend fun RoutingContext.respondUserNotFound() =
  respondJson(404, FailureBody(field = "userId", reason = "user not found"))
