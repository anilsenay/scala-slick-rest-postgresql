package com.anilsenay.utils

final case class AuthException(
                                private val message: String = "Unauthorized: Email or Password is wrong!",
                                private val cause: Throwable = None.orNull
                              ) extends Exception(message, cause)