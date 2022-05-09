package me.jason5lee.ktpost.common

import io.vertx.ext.web.RoutingContext
import me.jason5lee.resukt.Result

fun RoutingContext.getQueryParamOptionalInt(name: String): Result<Int?, String> {
  val values = queryParam(name)
  if (values.isEmpty()) {
    return Result.success(null)
  }
  if (values.size > 1) {
    return Result.failure("Query param '$name' has more than one value")
  }
  val value = values[0]
  return try {
    Result.success(value.toInt())
  } catch (e: NumberFormatException) {
    Result.failure("Query param '$name' is not a long")
  }
}

fun RoutingContext.getQueryParamInt(name: String): Result<Int, String> {
  val values = queryParam(name)
  if (values.isEmpty()) {
    return Result.failure("Query param '$name' is missing")
  }
  if (values.size > 1) {
    return Result.failure("Query param '$name' has more than one value")
  }
  val value = values[0]
  return try {
    Result.success(value.toInt())
  } catch (e: NumberFormatException) {
    Result.failure("Query param '$name' is not a long")
  }
}
