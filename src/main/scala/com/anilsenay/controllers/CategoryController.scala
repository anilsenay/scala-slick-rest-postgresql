package com.anilsenay.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.Category
import com.anilsenay.services.CategoryService
import spray.json._

import scala.util.{Failure, Success}

class CategoryController(dbService: CategoryService.type) extends BaseController {
  val route: Route = pathPrefix("api" / "category") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllCategories)
      }
    } ~
    post {
      pathEndOrSingleSlash {
        entity(as[Category]) { category =>
          authenticate { authUser =>
            if (authUser.isAdmin) {
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
            else complete(StatusCodes.Unauthorized)
          }
        }
      }
    } ~
    put {
      path(LongNumber) { id =>
        entity(as[Category]) { category =>
          authenticate { authUser =>
            if (authUser.isAdmin) {
              val updated = dbService.update(id, category.categoryName)
              onComplete(updated) {
                case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
                case Failure(e) => {
                  logger.error(s"Failed to update category ${id}", e)
                  complete(StatusCodes.InternalServerError)
                }
              }
            }
            else complete(StatusCodes.Unauthorized)
          }
        }
      }
    } ~
    delete {
      path(LongNumber) { id =>
        authenticate { authUser =>
          if (authUser.isAdmin) {
            val deleted = dbService.delete(id)
            onComplete(deleted) {
              case Success(updatedRows) => complete(JsObject("deletedRows" -> JsNumber(updatedRows)))
              case Failure(e) => {
                logger.error(s"Failed to delete category ${id}", e)
                complete(StatusCodes.InternalServerError)
              }
            }
          }
          else complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
}
