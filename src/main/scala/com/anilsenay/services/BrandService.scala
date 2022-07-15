package com.anilsenay.services

import com.anilsenay.models.Brand
import com.anilsenay.schema.BrandTable._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class BrandService(db: Database)(implicit ec: ExecutionContext) {
  def getAllBrands: Future[Seq[Brand]] = db.run(brands.result)

  def insertBrand(brandName: String) = {
    db.run(brands += Brand(None, brandName))
  }

  def update(id: Long, brandName: String) = {
    db.run(brands.filter(_.id === id).map(_.brandName).update(brandName))
  }

  def delete(id: Long): Future[Int] = {
    db.run(brands.filter(_.id === id).delete)
  }
}