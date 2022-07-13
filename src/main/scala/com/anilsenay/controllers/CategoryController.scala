package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.{Category, User}
import com.anilsenay.services.CategoryService
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class CategoryController(dbService: CategoryService) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "category") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllCategories)
      }
    } ~
    post {
      pathEndOrSingleSlash {
      entity(as[Category]) { category =>
        val saved = dbService.insertCategory(category.categoryName)
        onComplete(saved) {
          case Success(updatedRows) => {
            logger.info(s"Inserted category")
            complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
          }
          case Failure(e) => {
            logger.error(s"Failed to insert category", e)
            complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    put {
      path(LongNumber) { id =>
        entity(as[Category]) { category =>
          val updated = dbService.update(id, category.categoryName)
          onComplete(updated) {
            case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
            case Failure(e) => {
              logger.error(s"Failed to update category ${id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    delete {
      path(LongNumber) { id =>
        val deleted = dbService.delete(id)
        onComplete(deleted) {
          case Success(updatedRows) => complete(JsObject("deletedRows" -> JsNumber(updatedRows)))
          case Failure(e) => {
            logger.error(s"Failed to update category ${id}", e)
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}
