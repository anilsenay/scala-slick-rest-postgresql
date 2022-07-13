package com.anilsenay.schema

import com.anilsenay.models.Category
import slick.jdbc.PostgresProfile.api._

object CategoryTable {

  class Categories(tag: Tag) extends Table[Category](tag, "category") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def categoryName = column[String]("category_name")

    def * =
      (id, categoryName) <> ((Category.apply _).tupled, Category.unapply)
  }

  val categories = TableQuery[Categories]

}
