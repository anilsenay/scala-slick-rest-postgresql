package com.anilsenay.services

import com.anilsenay.utils.PostgresDb
import slick.dbio.NoStream
import slick.sql.{FixedSqlStreamingAction, SqlAction}

import scala.concurrent.Future

trait BaseService {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val db = PostgresDb.db

  protected implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }
  protected implicit def executeReadStreamFromDb[A](action: FixedSqlStreamingAction[Seq[A], A, _ <: slick.dbio.Effect]): Future[Seq[A]] = {
    db.run(action)
  }

}
