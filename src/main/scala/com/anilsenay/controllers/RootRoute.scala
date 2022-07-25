package com.anilsenay.controllers

import com.anilsenay.services.{AddressService, AuthService, BrandService, CategoryService, OrderService, ProductService, UserService}
import akka.http.scaladsl.server.Directives._

import akka.http.scaladsl.server.Route

object RootRoute {
  def apply(): Route = {
    new UserController(UserService).route ~
    new AddressController(AddressService).route ~
    new ProductController(ProductService).route ~
    new CategoryController(CategoryService).route ~
    new BrandController(BrandService).route ~
    new OrderController(OrderService).route ~
    new AuthController(AuthService).route
  }
}
