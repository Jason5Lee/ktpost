package me.jason5lee.ktpost.adminLogin

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result

class Implementation(private val mysql: MySQLPool) : AdminLogin {
  override suspend fun invoke(command: Command): Result<Unit, Failure> {
    mysql.preparedQuery("SELECT password FROM admins WHERE admin_id = ?")
      .execute(Tuple.of(command.id.value))
      .await()
      .let { rows ->
        val row = rows.firstOrNull() ?: return Result.failure(Failure.IdOrPasswordIncorrect)
        return if (command.password.verifyEncrypted(row.getNonNullString("password"))) {
          Result.success(Unit)
        } else Result.failure(Failure.IdOrPasswordIncorrect)
      }
  }
}
