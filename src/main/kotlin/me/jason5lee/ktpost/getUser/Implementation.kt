package me.jason5lee.ktpost.getUser

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.Time
import me.jason5lee.ktpost.common.UserName
import me.jason5lee.ktpost.common.getNonNullLong
import me.jason5lee.ktpost.common.getStringModel
import me.jason5lee.resukt.Result

class Implementation(val mysql: MySQLPool) : GetUser {
  override suspend fun invoke(input: Query): Result<UserInfo, Failure> {
    val row = mysql.preparedQuery("SELECT user_name, creation_time FROM users WHERE user_id=?")
      .execute(Tuple.of(input.value))
      .await()
      .firstOrNull() ?: return Result.failure(Failure.UserNotFound)
    return Result.success(
        UserInfo(
      name = row.getStringModel("user_name") { UserName.new(it) },
      creation = Time(utc = row.getNonNullLong("creation_time"))
    )
    )
  }
}
