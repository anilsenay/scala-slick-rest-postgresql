package com.anilsenay.schema

import com.anilsenay.models.Product
import slick.jdbc.PostgresProfile.api._

object ProductTable {

  class Products(tag: Tag) extends Table[Product](tag, "product") {
    def id = column[Option[String]]("id", O.PrimaryKey)
    def name = column[String]("name")
    def coverPhotoIndex = column[Int]("cover_photo_index")
    def information = column[String]("information")
    def price = column[Double]("price")
    def salePrice = column[Double]("sale_price")

    def * =
      (id, name, coverPhotoIndex, information, price, salePrice) <> ((Product.apply _).tupled, Product.unapply)
  }

  val products = TableQuery[Products]

}
