package com.anilsenay.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.Address
import com.anilsenay.services.AddressService
import spray.json._

import scala.util.{Failure, Success}

class AddressController(dbService: AddressService.type) extends BaseController {
  val route: Route = pathPrefix("api" / "address") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllAddress)
      } ~
        path(LongNumber) { addressId =>
          complete(dbService.getAddress(addressId))
        }
    } ~
    post {
      pathEndOrSingleSlash {
        entity(as[Address]) { address =>
          val saved = dbService.insertAddress(address.title, address.city, address.region, address.zipcode, address.fullAddress)
          onComplete(saved) {
            case Success(savedAddress) => {
              logger.info(s"Inserted person with id:${savedAddress.id}")
              complete(savedAddress)
            }
            case Failure(e) => {
              logger.error(s"Failed to insert a person ${address.id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    put {
      path(LongNumber) { id =>
        entity(as[Address]) { address =>
          val updated = dbService.update(id, address)
          onComplete(updated) {
            case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
            case Failure(e) => {
              logger.error(s"Failed to update a person ${id}", e)
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
            logger.error(s"Failed to delete a person ${id}", e)
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}
