package com.anilsenay.schema

import com.anilsenay.models.ProductSize
import com.anilsenay.schema.ProductTable.Products
import slick.jdbc.PostgresProfile.api._

object ProductSizeTable {

  class ProductSizes(tag: Tag) extends Table[ProductSize](tag, "product_size") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def size = column[String]("size")
    def productId = column[Option[Long]]("product_id")

    def * =
      (id, size, productId) <> ((ProductSize.apply _).tupled, ProductSize.unapply)

    def product = foreignKey("fk_product", productId, TableQuery[Products])(_.id, onDelete=ForeignKeyAction.Cascade)

  }

  val productSizes = TableQuery[ProductSizes]

}
