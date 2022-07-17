package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class ProductSize(id: Option[Long], size: String, product_id: Option[Long])

object ProductSize extends DefaultJsonProtocol {
  implicit val productImageFormat = jsonFormat3(ProductSize.apply)
}
