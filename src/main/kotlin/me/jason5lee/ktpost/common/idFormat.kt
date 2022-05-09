package me.jason5lee.ktpost.common


import me.jason5lee.resukt.Result
import java.util.*

private val base64Decoder = Base64.getUrlDecoder()
private val base64Encoder = Base64.getUrlEncoder().withoutPadding()

fun parseId(s: String): Result<Long, String> {
  val bytes = try {
    base64Decoder.decode(s)
  } catch (e: IllegalArgumentException) {
    return Result.failure(e.toString())
  }

  if (bytes.size != 8) {
    Result.failure("wrong length")
  }

  var value: Long = 0
  for (i in 0 until 8) {
    value = value or ((bytes[i].toLong() and 0xFF) shl (i * 8))
  }
  return Result.success(value)
}

fun formatId(s: Long): String {
  val bytes = ByteArray(8)
  for (i in 0 until 8) {
    bytes[i] = ((s ushr (i * 8)) and 0xFF).toByte()
  }
  return base64Encoder.encodeToString(bytes)
}
