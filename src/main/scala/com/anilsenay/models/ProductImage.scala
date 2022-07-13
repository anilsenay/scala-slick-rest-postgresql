package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class ProductImage(id: Option[Long], url: String, product_id: Option[Long])

object ProductImage extends DefaultJsonProtocol {
  implicit val productImageFormat = jsonFormat3(ProductImage.apply)
}
