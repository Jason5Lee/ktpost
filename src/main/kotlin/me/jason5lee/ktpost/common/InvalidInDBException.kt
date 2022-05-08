package me.jason5lee.ktpost.common

import me.jason5lee.resukt.Result
import me.jason5lee.resukt.getOrElse

fun io.vertx.sqlclient.Row.throwInvalidInDB(column: String, reason: String): Nothing =
  throw Exception("invalid value found in DB, column=`$column` row=`${this@throwInvalidInDB}`: $reason")
fun io.vertx.sqlclient.Row.getNonNullString(column: String): String =
  getString(column) ?: throw Exception("invalid value found in DB, column=`$column` row=`${this@getNonNullString}`: value is null")

inline fun <R> io.vertx.sqlclient.Row.getStringModel(column: String, newModel: (String) -> Result<R, String>): R {
  val value = getNonNullString(column)
  return newModel(value).getOrElse { reason ->
    throw Exception("invalid value found in DB, column=`$column` row=`${this@getStringModel}`: $reason")
  }
}

fun io.vertx.sqlclient.Row.getNonNullLong(column: String): Long =
  getLong(column) ?: throw Exception("invalid value found in DB, column=`$column`, value is null")

inline fun <R> io.vertx.sqlclient.Row.getLongModel(column: String, newModel: (Long) -> Result<R, String>): R {
  val value = getNonNullLong(column)
  return newModel(value).getOrElse { reason ->
    throw Exception("invalid value found in DB, column=`$column` row=`${this@getLongModel}`: $reason")
  }
}
