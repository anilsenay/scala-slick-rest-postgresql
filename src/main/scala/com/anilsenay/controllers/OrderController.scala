package com.anilsenay.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.OrderPost
import com.anilsenay.services.OrderService
import spray.json._

import scala.util.{Failure, Success}

class OrderController(dbService: OrderService.type) extends BaseController {
  val route: Route = pathPrefix("api" / "order") {
    get {
      path(LongNumber) {
        (orderId) => {
          authenticate { authUser =>
            var order = for {
              foundOrder <- dbService.findOrderByUserId(orderId, authUser.id)
              order <- {
                val id = if(foundOrder.nonEmpty) foundOrder.get.id.get else 0
                dbService.getOrder(id)
              }
            } yield {
              if (Some(order).isEmpty) throw new NoSuchElementException() else order
            }
            if(authUser.isAdmin) order = dbService.getOrder(orderId)
            onComplete(order) {
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
        }
      } ~
      pathEndOrSingleSlash {
        parameter("user".as[Long]) { userId =>
          authenticate { authUser =>
            if(userId == authUser.id || authUser.isAdmin) {
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
            else complete(StatusCodes.Unauthorized)
          }
        }
      }
    } ~
    post {
      entity(as[OrderPost]) { order: OrderPost =>
        authenticate { authUser =>
          if((order.userId.nonEmpty && order.userId.get == authUser.id) || authUser.isAdmin) {
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
          else complete(StatusCodes.Unauthorized)
        }
      }
    } ~
    put {
      path(LongNumber / IntNumber) { (id, status) =>
        authenticate { authUser =>
         if(authUser.isAdmin) {
           val statusList = List("preparing", "canceled", "completed", "shipping")
           val updated = dbService.update(id, status)
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
         else complete(StatusCodes.Unauthorized)
        }
      } ~
      path(LongNumber / "cancel") { (id) =>
        authenticate { authUser =>
          val updated = dbService.cancelOrder(id)
          var cancalled = for {
            foundOrder <- dbService.findOrderByUserId(id, authUser.id)
            order <- {
              val id = if(foundOrder.nonEmpty) foundOrder.get.id.get else 0
              dbService.cancelOrder(id)
            }
          } yield {
            if (order == 0) throw new NoSuchElementException() else order
          }
          if(authUser.isAdmin) cancalled = dbService.update(id, 1)
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
      }
    } ~
    delete {
      path(LongNumber) { id =>
        authenticate { authUser =>
          if(authUser.isAdmin) {
            val deleted = dbService.delete(id)
            onComplete(deleted) {
              case Success(updatedRows) => complete(JsObject("deletedRows" -> JsNumber(updatedRows)))
              case Failure(e) => {
                logger.error(s"Failed to update a person ${id}", e)
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
