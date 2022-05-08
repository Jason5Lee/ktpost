package me.jason5lee.ktpost.common

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class FailureBody(
  val field: String? = null,
  val reason: String,
)
