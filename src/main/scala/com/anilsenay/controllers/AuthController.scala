package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.{AuthContent, JWTTokenOnly, UserRegister}
import com.anilsenay.services.AuthService
import com.anilsenay.utils.AuthException
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class AuthController(dbService: AuthService.type) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "auth") {
    post {
      path("login") {
        entity(as[AuthContent]) { authContent =>
          val searchedUser = dbService.login(authContent.email, authContent.password)
          onComplete(searchedUser) {
            case Success(foundUser) => {
              logger.info(s"Logged in :${foundUser}")
              complete(foundUser)
            }
            case Failure(e) => {
              logger.error(s"Error:", e)
              e match {
                case e: AuthException => complete(StatusCodes.Unauthorized)
                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~
      path("register") {
        entity(as[UserRegister]) { registerContent =>
          val registeredUser = dbService.register(registerContent)
          onComplete(registeredUser) {
            case Success(user) => {
              logger.info(s"Registered as :${user}")
              complete(user)
            }
            case Failure(e) => {
              logger.error(s"Error:", e)
              e match {
                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~
      path("validate") {
        entity(as[JWTTokenOnly]) { token =>
          val isValid: Boolean = dbService.validateToken(token.token)
          if(isValid) complete(StatusCodes.OK) else complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
}
