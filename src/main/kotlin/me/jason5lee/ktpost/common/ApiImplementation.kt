package me.jason5lee.ktpost.common

import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun api(
  crossinline path: Router.() -> Route,
  needRequestBody: Boolean = false,
  crossinline implementation: suspend (RoutingContext) -> Unit,
): ApiImplementation =
  object : ApiImplementation {
    override fun addRoute(router: Router, scope: CoroutineScope, logger: mu.KLogger) {
      var route = router.path()
      if (needRequestBody) {
        route = route.handler(BodyHandler.create())
      }
      route.handler { ctx ->
        scope.launch {
          try {
            implementation(ctx)
          } catch (e: Exception) {
            logger.error { e.stackTraceToString() }
            ctx.respondJson(500, "")
          }
        }
      }
    }
  }

interface ApiImplementation {
  fun addRoute(router: Router, scope: CoroutineScope, logger: mu.KLogger)
}
