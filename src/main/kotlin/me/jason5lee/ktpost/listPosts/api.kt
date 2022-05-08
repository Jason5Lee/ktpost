package me.jason5lee.ktpost.listPosts

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.andThen
import me.jason5lee.resukt.getOrElse

fun listPostsApi(listPosts: ListPosts) = api(
  path = { get("/posts") },
) { ctx ->
  val input = Query(
    offset = ctx.getQueryParamOptionalInt("offset")
      .andThen { Offset.new(it) }
      .getOrElse { return@api ctx.respondJson(400, FailureBody(field = "offset", reason = it )) },
    size = ctx.getQueryParamInt("size")
      .andThen { Size.new(it) }
      .getOrElse { return@api ctx.respondJson(400, FailureBody(field = "size", reason = it )) },
  )
  val output = listPosts(input)

  @Suppress("unused")
  class PostInfoDto(
    val id: String,
    val title: String,
    val creatorId: String,
    val creatorName: String,
    val creationTime: Long,
  )

  ctx.json(output.posts.map {
    PostInfoDto(
      id = formatId(it.id.value),
      title = it.title.value,
      creatorId = formatId(it.creator.id.value),
      creatorName = it.creator.name.value,
      creationTime = it.creation.utc,
    )
  })
}
