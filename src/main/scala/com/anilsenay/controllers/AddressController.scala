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
    authenticate { authUser =>
      get {
        pathEndOrSingleSlash {
          if (authUser.isAdmin) complete(dbService.getAllAddress)
          else complete(StatusCodes.Unauthorized)
        } ~
        path(LongNumber) { addressId =>
          var address = for {
            findAddress <- dbService.findAddressByUserId(addressId, authUser.id)
            address <- {
              val id = if(findAddress.nonEmpty) findAddress.get.address_id.get else 0
              dbService.getAddress(id)
            }
          } yield {
            if (address.isEmpty) throw new NoSuchElementException() else address
          }
          if(authUser.isAdmin) address = dbService.getAddress(addressId)
          onComplete(address) {
            case Success(foundAddress) => complete(foundAddress)
            case Failure(e) => {
              logger.error(s"Failed to find address ${addressId}", e)
              e match {
                case e: NoSuchElementException => complete(StatusCodes.NotFound)
                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~
      post {
        pathEndOrSingleSlash {
          entity(as[Address]) { address =>
            if (authUser.isAdmin) {
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
            else complete(StatusCodes.Unauthorized)
          }
        }
      } ~
      put {
        path(LongNumber) { id =>
          entity(as[Address]) { address =>
            var updated = for {
              findAddress <- dbService.findAddressByUserId(id, authUser.id)
              updated <- {
                val id = if(findAddress.nonEmpty) findAddress.get.address_id.get else 0
                dbService.update(id, address)
              }
            } yield {
              if (updated == 0) throw new NoSuchElementException()
              updated
            }
            if(authUser.isAdmin) updated = dbService.update(id, address)
            onComplete(updated) {
              case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
              case Failure(e) => {
                logger.error(s"Failed to update a person ${id}", e)
                e match {
                  case e: NoSuchElementException => complete(StatusCodes.NotFound)
                  case _ => complete(StatusCodes.InternalServerError)
                }
              }
            }
          }
        }
      } ~
      delete {
        path(LongNumber) { id =>
          var deleted = for {
            findAddress <- dbService.findAddressByUserId(id, authUser.id)
            deleted <- {
              val id = if(findAddress.nonEmpty) findAddress.get.address_id.get else 0
              dbService.delete(id)
            }
          } yield {
            if (deleted == 0) throw new NoSuchElementException()
            deleted
          }
          if(authUser.isAdmin) deleted = dbService.delete(id)
          onComplete(deleted) {
            case Success(updatedRows) => complete(JsObject("deletedRows" -> JsNumber(updatedRows)))
            case Failure(e) => {
              logger.error(s"Failed to delete a person ${id}", e)
              e match {
                case e: NoSuchElementException => complete(StatusCodes.NotFound)
                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      }
    }
  }
}
