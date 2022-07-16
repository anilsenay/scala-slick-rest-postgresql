package com.anilsenay.controllers

import com.anilsenay.services.{AddressService, BrandService, CategoryService, OrderService, ProductService, UserService}
import slick.jdbc.PostgresProfile.api._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route

object RootRoute {
  def apply(): Route = {
    new UserController(UserService).route ~
    new AddressController(AddressService).route ~
    new ProductController(ProductService).route ~
    new CategoryController(CategoryService).route ~
    new BrandController(BrandService).route ~
    new OrderController(OrderService).route
  }
}
