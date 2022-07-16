package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class OrderProduct(order_id: Option[Long],  product_id: Option[Long], quantity: Int, size: Option[String], price: Double)

object OrderProduct extends DefaultJsonProtocol {
  implicit val orderProductFormat = jsonFormat5(OrderProduct.apply)
}

case class OrderProductPost(id: Long, quantity: Int, size: Option[String])

object OrderProductPost extends DefaultJsonProtocol {
  implicit val orderProductFormat = jsonFormat3(OrderProductPost.apply)
}

case class OrderFullProduct(product: FullProduct, size: Option[String], price: Double)

object OrderFullProduct extends DefaultJsonProtocol {
  implicit val orderProductFormat = jsonFormat3(OrderFullProduct.apply)
}