package com.anilsenay.schema

import com.anilsenay.models.Brand
import slick.jdbc.PostgresProfile.api._

object BrandTable {

  class Brands(tag: Tag) extends Table[Brand](tag, "brand") {
    def id = column[Option[String]]("id", O.PrimaryKey)
    def name = column[String]("name")

    def * =
      (id, name) <> ((Brand.apply _).tupled, Brand.unapply)
  }

  val brands = TableQuery[Brands]

}
