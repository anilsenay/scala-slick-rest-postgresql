package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.{Address, Order, OrderPost, User, UserWithAddress}
import com.anilsenay.services.{AddressService, OrderService, UserService}
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class OrderController(dbService: OrderService.type) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "order") {
    get {
      path(LongNumber) {
        (orderId) => {
          val q = dbService.getOrder(orderId)
          onComplete(q) {
            case Success(result) => {
              complete(result)
            }
            case Failure(e) => {
              logger.error(s"Failed to fetch user ${orderId}", e)
              e match {
                case e: NoSuchElementException => complete(StatusCodes.NotFound)
                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~
      parameter("user".as[Long]) { userId =>
        val q = dbService.getUserOrders(userId)
        onComplete(q) {
          case Success(result) => {
            complete(result)
          }
          case Failure(e) => {
            logger.error(s"Failed to fetch user ${userId}", e)
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    } ~
    post {
      entity(as[OrderPost]) { order: OrderPost =>
        val saved = dbService.insertOrder(order)
        onComplete(saved) {
          case Success(savedProduct) => {
            logger.info(s"Inserted product with id:${savedProduct.id}")
            complete(savedProduct)
          }
          case Failure(e) => {
            logger.error(s"Failed to insert product", e)
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    } ~
    put {
      path(LongNumber / IntNumber) { (id, status) =>
        val statusList = List("preparing", "canceled", "completed", "shipping")
        val updated = dbService.update(id, statusList(status))
        onComplete(updated) {
          case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
          case Failure(e) => {
            logger.error(s"Failed to update a person ${id}", e)
            e match {
              case e: IndexOutOfBoundsException => complete(StatusCodes.BadRequest)
              case _ => complete(StatusCodes.InternalServerError)
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
            logger.error(s"Failed to update a person ${id}", e)
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}
