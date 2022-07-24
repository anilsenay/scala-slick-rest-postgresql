package com.anilsenay.utils

object SaltGenerator {
  def get: Int => String = (num: Int) => new scala.util.Random(num).alphanumeric.take(num).mkString
}
