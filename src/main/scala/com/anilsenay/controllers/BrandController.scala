package com.anilsenay.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.Brand
import com.anilsenay.services.BrandService
import spray.json._

import scala.util.{Failure, Success}

class BrandController(dbService: BrandService.type) extends BaseController {
  val route: Route = pathPrefix("api" / "brand") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllBrands)
      }
    } ~
    post {
      pathEndOrSingleSlash {
        entity(as[Brand]) { brand =>
          authenticate { authUser =>
            if (authUser.isAdmin) {
              val saved = dbService.insertBrand(brand.brandName)
              onComplete(saved) {
                case Success(updatedRows) => {
                  logger.info(s"Inserted brand")
                  complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
                }
                case Failure(e) => {
                  logger.error(s"Failed to insert brand", e)
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
        entity(as[Brand]) { brand =>
          authenticate { authUser =>
            if (authUser.isAdmin) {
              val updated = dbService.update(id, brand.brandName)
              onComplete(updated) {
                case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
                case Failure(e) => {
                  logger.error(s"Failed to update brand ${id}", e)
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
                logger.error(s"Failed to delete brand ${id}", e)
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
