package me.jason5lee.ktpost.createPost

import io.vertx.core.json.Json
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun createPostApi(createPost: CreatePost, auth: Auth) = api(
  path = { put("/post") },
  needRequestBody = true
) { ctx ->
  val creator = auth.authUserOnly(ctx).getOrElse { return@api ctx.respondAuthFailure(it) }

  class RequestDto(
    val title: String,
    val post: String?,
    val url: String?,
  )

  val req = Json.decodeValue(ctx.bodyAsString, RequestDto::class.java)
  val input = Command(
    creator = creator,
    title = PostTitle.new(req.title).getOrElse { reason ->
      return@api ctx.respondJson(statusCode = 422, FailureBody(field = "title", reason = reason))
    },
    content = when {
      req.post != null && req.url == null -> PostContent.Post(req.post)
      req.post == null && req.url != null -> PostContent.Url.create(req.url).getOrElse { reason ->
        return@api ctx.respondJson(statusCode = 422, FailureBody(field = "url", reason = reason))
      }
      else -> return@api ctx.respondJson(
        statusCode = 400,
        FailureBody(reason = "url and post should present exact once")
      )
    }
  )
  val output = createPost(input).getOrElse { failure ->
    when (failure) {
      Failure.DuplicatedTitle -> return@api ctx.respondJson(
        statusCode = 409,
        FailureBody(field = "title", reason = "duplicated title")
      )
    }
  }

  val postId = formatId(output.value)

  ctx.response().putHeader("Location", "/post/${postId}")
  ctx.response().statusCode = 201
  @Suppress("unused")
  class ResponseDto(
    val postId: String,
  )
  ctx.json(
    ResponseDto(
      postId = postId
    )
  )
}
