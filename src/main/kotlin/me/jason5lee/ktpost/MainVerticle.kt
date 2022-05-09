package me.jason5lee.ktpost

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.relops.snowflake.Snowflake
import io.vertx.config.ConfigRetriever
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import me.jason5lee.ktpost.common.*
import mu.KotlinLogging

class MainVerticle : CoroutineVerticle() {
  override suspend fun start() {
    System.setProperty("org.vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory::class.java.name)
    DatabindCodec.mapper().registerKotlinModule()
    DatabindCodec.prettyMapper().registerKotlinModule()

    val config = ConfigRetriever.create(vertx).config.await()

    val logger = KotlinLogging.logger {}
    val secretConf = config.getString("secret")
    val mysqlUri = config.getString("mysql")
    val expireSecs = config.getLong("expireSecs", 60 * 30)
    val cost = config.getInteger("cost", 7)

    val jwtAuth = JWTAuth.create(
      vertx, JWTAuthOptions()
        .addPubSecKey(
          PubSecKeyOptions()
            .setAlgorithm("HS256")
            .setBuffer(secretConf)
        )
    )
    val server = vertx.createHttpServer()

    val router = Router.router(vertx)

    val deps = Dependencies().apply {
      setDependency(logger)
      setDependency(Auth(logger, jwtAuth, "ktpost", expireSecs))
      setDependency(MySQLPool.pool(vertx, mysqlUri))
      setDependency(Snowflake(0))
      setDependency(Encryptor(cost))
    }
    routeAll(this, router, deps)

    server
      .requestHandler(router)
      .listen(8888)
      .await()
    println("HTTP server started on port 8888")
  }
}


