package me.jason5lee.ktpost.deletePost

import io.vertx.ext.web.RoutingContext
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun deletePostApi(deletePost: DeletePost, auth: Auth) = api(
  path = { delete("/post/:id") },
  needRequestBody = true,
) { ctx ->
  val caller = auth.getIdentity(ctx).getOrElse { return@api ctx.respondAuthFailure(it) }
  val id = PostId(
    parseId(ctx.pathParam("id") ?: return@api ctx.respondNoPostId())
      .getOrElse { return@api ctx.respondJson(422, FailureBody(reason = it)) }
  )
  deletePost(Command(caller, id)).getOrElse { return@api ctx.respondFailure(it) }
  ctx.response().statusCode = 204
  ctx.end()
}

private suspend fun RoutingContext.respondNoPostId(): Unit =
  respondJson(
    statusCode = 400, // Bad Request
    body = FailureBody(field = "postId", reason = "parameter not provided")
  )

private suspend fun RoutingContext.respondFailure(failure: Failure) = when (failure) {
  Failure.Unauthorized -> respondAuthFailure(AuthFailure.Unauthorized)
  Failure.PostNotFound -> respondJson(404, FailureBody(field = "id", reason = "Post not found"))
}
