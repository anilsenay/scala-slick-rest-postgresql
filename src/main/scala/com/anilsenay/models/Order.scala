package com.anilsenay.models

import com.anilsenay.utils.BaseJsonProtocol

import java.sql.Timestamp

case class Order(
                  id: Option[Long],
                  userId: Option[Long],
                  addressId: Option[Long],
                  totalPrice: Double,
                  status: Int,
                  createdAt: Timestamp,
                  updatedAt: Timestamp,
                )

object Order extends BaseJsonProtocol {
  implicit val orderFormat = jsonFormat7(Order.apply)
}

case class FullOrder(
                  id: Option[Long],
                  user: Option[User],
                  address: Option[Address],
                  totalPrice: Double,
                  status: Int,
                  createdAt: Timestamp,
                  updatedAt: Timestamp,
                  products: Seq[OrderFullProduct]
                )

object FullOrder extends BaseJsonProtocol {
  implicit val orderFormat = jsonFormat8(FullOrder.apply)
}

case class OrderPost(
                  userId: Option[Long],
                  addressId: Option[Long],
                  products: Seq[OrderProductPost]
                )

object OrderPost extends BaseJsonProtocol {
  implicit val orderFormat = jsonFormat3(OrderPost.apply)
}