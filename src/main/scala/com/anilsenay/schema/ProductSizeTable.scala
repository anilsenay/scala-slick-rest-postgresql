package com.anilsenay.schema

import com.anilsenay.models.ProductSize
import slick.jdbc.PostgresProfile.api._

object ProductSizeTable {

  class ProductSizes(tag: Tag) extends Table[ProductSize](tag, "product_size") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def size = column[String]("size")
    def productId = column[Option[Long]]("product_id")

    def * =
      (id, size, productId) <> ((ProductSize.apply _).tupled, ProductSize.unapply)
  }

  val productSizes = TableQuery[ProductSizes]

}
