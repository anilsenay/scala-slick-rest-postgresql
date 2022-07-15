package com.anilsenay.services

import com.anilsenay.models.Category
import com.anilsenay.schema.CategoryTable._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object CategoryService extends BaseService {
  def getAllCategories: Future[Seq[Category]] = db.run(categories.result)

  def insertCategory(categoryName: String) = {
    db.run(categories += Category(None, categoryName))
  }

  def update(id: Long, categoryName: String) = {
    db.run(categories.filter(_.id === id).map(_.categoryName).update(categoryName))
  }

  def delete(id: Long): Future[Int] = {
    db.run(categories.filter(_.id === id).delete)
  }
}