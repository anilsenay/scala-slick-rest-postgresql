package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class ProductImage(id: Option[String], url: String, product_id: Option[String])

object ProductImage extends DefaultJsonProtocol {
  implicit val productImageFormat = jsonFormat3(ProductImage.apply)
}
