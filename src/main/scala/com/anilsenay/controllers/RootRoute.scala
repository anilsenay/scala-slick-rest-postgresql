package com.anilsenay.controllers

import com.anilsenay.services.UserService
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

object RootRoute {
  def apply(db: Database)(implicit ec: ExecutionContext): UserController = {
    new UserController(new UserService(db))
  }
}
