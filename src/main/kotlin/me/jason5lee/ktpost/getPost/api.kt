package me.jason5lee.ktpost.getPost

import com.fasterxml.jackson.annotation.JsonInclude
import io.vertx.ext.web.RoutingContext
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun getPostApi(getPost: GetPost) = api(
  path = { get("/post/:postId") }
) { ctx ->
  val input = PostId(
    parseId(ctx.pathParam("postId") ?: return@api ctx.respondNoPostId())
      .getOrElse { return@api ctx.respondPostNotFound() }
  )
  val output = getPost(input).getOrElse {
    return@api when (it) {
      Failure.PostNotFound -> ctx.respondPostNotFound()
    }
  }

  val (post, url) = when (val content = output.content) {
    is PostContent.Post -> Pair(content.text, null)
    is PostContent.Url -> Pair(null, content.url.toString())
  }

  @Suppress("unused")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class ResponseDto(
    val creatorId: String,
    val creatorName: String,
    val creationTime: Long,
    val title: String,
    val post: String? = null,
    val url: String? = null,
    val lastModified: Long? = null,
  )

  ctx.json(
    ResponseDto(
      creatorId = formatId(output.creator.id.value),
      creatorName = output.creator.name.value,
      creationTime = output.creation.utc,
      title = output.title.value,
      post = post,
      url = url,
      lastModified = output.lastModified?.utc,
    )
  )
}

private suspend fun RoutingContext.respondNoPostId(): Unit =
  respondJson(
    statusCode = 400, // Bad Request
    body = FailureBody(field = "postId", reason = "parameter not provided")
  )

private suspend fun RoutingContext.respondPostNotFound(): Unit =
  respondJson(
    statusCode = 404, // Not Found
    body = FailureBody(
      field = "postId",
      reason = "post not found"
    )
  )
