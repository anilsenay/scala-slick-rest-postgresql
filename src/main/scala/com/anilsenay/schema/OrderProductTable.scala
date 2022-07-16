package com.anilsenay.schema

import com.anilsenay.models.OrderProduct
import com.anilsenay.schema.OrderTable.Orders
import com.anilsenay.schema.ProductTable.Products
import com.anilsenay.utils.SelectedProfile.api._

object OrderProductTable {

  class OrderProducts(tag: Tag) extends Table[OrderProduct](tag, "order_product") {
    def orderId = column[Option[Long]]("order_id")
    def productId = column[Option[Long]]("product_id")
    def quantity = column[Int]("quantity")
    def size = column[Option[String]]("size")
    def price = column[Double]("price")

    def * =
      (orderId, productId, quantity, size, price) <> ((OrderProduct.apply _).tupled, OrderProduct.unapply)

    def order = foreignKey("fk_order", orderId, TableQuery[Orders])(_.id, onDelete=ForeignKeyAction.Cascade)
    def product = foreignKey("fk_product", productId, TableQuery[Products])(_.id)

  }

  val orderProducts = TableQuery[OrderProducts]

}
