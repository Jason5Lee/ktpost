package me.jason5lee.ktpost.editPost

import io.vertx.core.json.Json
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun editPostApi(editPost: EditPost, auth: Auth) = api(
  path = { post("/post/:postId/edit") },
  needRequestBody = true,
) { ctx ->
  val creator = auth.authUserOnly(ctx).getOrElse { return@api ctx.respondAuthFailure(it) }
  val postId = ctx.pathParam("postId")
    .let { it ?: return@api ctx.respondJson(statusCode = 400, FailureBody(field = "postId", reason = "post")) }
    .let {
      parseId(it).getOrElse { reason ->
        return@api ctx.respondJson(
          statusCode = 422,
          FailureBody(field = "postId", reason = "invalid postId: $reason")
        )
      }
    }
    .let { PostId(it) }

  @Suppress("unused")
  class RequestDto(
    val post: String? = null,
    val url: String? = null,
  )

  val req = Json.decodeValue(ctx.bodyAsString, RequestDto::class.java)
  val content =
    when {
      req.post != null && req.url == null -> PostContent.Post(req.post)
      req.post == null && req.url != null -> PostContent.Url.create(req.url).getOrElse { reason ->
        return@api ctx.respondJson(statusCode = 422, FailureBody(field = "url", reason = reason))
      }
      else -> return@api ctx.respondJson(
        statusCode = 400,
        FailureBody(field = "url,post", reason = "url and post should present exact once")
      )
    }
  val input = Command(
    creator = creator,
    id = postId,
    newContent = content,
  )
  editPost(input).getOrElse { failure ->
    return@api when (failure) {
      Failure.PostNotFound -> ctx.respondJson(
        statusCode = 404, // Not Found
        FailureBody(field = "postId", reason = "post not found")
      )
      Failure.NotCreator -> ctx.respondJson(
        statusCode = 403, // Forbidden
        FailureBody(reason = "not creator")
      )
      Failure.PostTypeDiffers -> ctx.respondJson(
        statusCode = 422, // Unprocessable Entity
        FailureBody(reason = "post type differs")
      )
    }
  }
  ctx.response().statusCode = 204 // No Content
  ctx.response().end()
}
