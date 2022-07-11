package com.anilsenay.controllers

import com.anilsenay.services.{AddressService, UserService}
import slick.jdbc.PostgresProfile.api._
import akka.http.scaladsl.server.Directives._
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route

object RootRoute {
  def apply(db: Database)(implicit ec: ExecutionContext): Route = {
    new UserController(new UserService(db)).route ~ new AddressController(new AddressService(db)).route
  }
}
