package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class ProductSize(id: Option[String], size: String, product_id: Option[String])

object ProductSize extends DefaultJsonProtocol {
  implicit val productImageFormat = jsonFormat3(ProductSize.apply)
}
