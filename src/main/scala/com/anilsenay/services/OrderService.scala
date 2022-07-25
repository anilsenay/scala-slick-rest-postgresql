package com.anilsenay.services

import com.anilsenay.models.{FullOrder, FullProduct, Order, OrderFullProduct, OrderPost, OrderProduct}
import com.anilsenay.schema.AddressTable._
import com.anilsenay.schema.BrandTable.brands
import com.anilsenay.schema.CategoryTable.categories
import com.anilsenay.schema.OrderProductTable._
import com.anilsenay.schema.OrderTable._
import com.anilsenay.schema.ProductImageTable.productImages
import com.anilsenay.schema.ProductTable.products
import com.anilsenay.schema.UsersTable._
import com.anilsenay.utils.SelectedProfile.api._

import java.sql.Timestamp
import scala.concurrent.Future

object OrderService extends BaseService {

  def getOrder(id: Long) = {
    db.run {
      val orderQuery = for {
        order <- orders.filter(_.id === id)
        address <- addresses.filter(_.id === order.addressId)
        user <- users.filter(_.id === order.userId)
        orderProduct <- orderProducts.filter(_.orderId === order.id)
        product <- products.filter(_.id === orderProduct.productId)
        brand <- brands.filter(_.id === product.brandId)
        category <- categories.filter(_.id === product.categoryId)
      } yield (order, address, user, (product, brand, category, orderProduct))

      val withPhotos = for {
        (order, photos) <- orderQuery.joinLeft(productImages).on(_._4._1.id === _.productId)
      } yield (order, photos)

      withPhotos.result
    }.map {
      tuples =>
        tuples.groupBy(_._1._1).map{
          case (o, tuples) => {
            val address = tuples.map(_._1._2).headOption
            val user = tuples.map(_._1._3).headOption
            val p = tuples.map(_._1._4._1)
            val products = tuples.groupBy(_._1._4._1).map {
              case (p, others) =>
                val brand = others.map(_._1._4._2).headOption
                val category = others.map(_._1._4._3).headOption
                val photos = others.map(_._2.map(_.url)).distinct.flatten
                val orderDetails = others.map(_._1._4._4).head

                val fp = FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, brand, category, photos, Seq(), p.createdAt)
                OrderFullProduct(fp, orderDetails.size, orderDetails.price)
            }.toSeq

            FullOrder(o.id, user, address, o.totalPrice, o.status, o.createdAt, o.updatedAt, products)
          }
        }.toSeq.head
    }
  }

  def getUserOrders(userId: Long) = {
    db.run {
      val orderQuery = for {
        order <- orders
        address <- addresses.filter(_.id === order.addressId)
        user <- users.filter(_.id === userId).filter(_.id === order.userId)
        orderProduct <- orderProducts.filter(_.orderId === order.id)
        product <- products.filter(_.id === orderProduct.productId)
        brand <- brands.filter(_.id === product.brandId)
        category <- categories.filter(_.id === product.categoryId)
      } yield (order, address, user, (product, brand, category, orderProduct))

      val withPhotos = for {
        (order, photos) <- orderQuery.joinLeft(productImages).on(_._4._1.id === _.productId)
      } yield (order, photos)

      withPhotos.result
    }.map {
      tuples =>
        tuples.groupBy(_._1._1).map{
          case (o, tuples) => {
            val address = tuples.map(_._1._2).headOption
            val user = tuples.map(_._1._3).headOption
            val p = tuples.map(_._1._4._1)
            val products = tuples.groupBy(_._1._4._1).map {
              case (p, others) =>
                val brand = others.map(_._1._4._2).headOption
                val category = others.map(_._1._4._3).headOption
                val photos = others.map(_._2.map(_.url)).distinct.flatten
                val orderDetails = others.map(_._1._4._4).head

                val fp = FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, brand, category, photos, Seq(), p.createdAt)
                OrderFullProduct(fp, orderDetails.size, orderDetails.price)
            }.toSeq

            FullOrder(o.id, user, address, o.totalPrice, o.status, o.createdAt, o.updatedAt, products)
          }
        }.toSeq
    }
  }

  def findOrderByUserId(addressId: Long, userId: Long): Future[Option[Order]] = {
    db.run {
      orders.filter(_.id === addressId).filter(_.userId === userId).result.headOption
    }
  }

  def insertOrder(order: OrderPost) = {
    val productIds = order.products.map(_.id)
    val now = new Timestamp(System.currentTimeMillis())
    db.run {
      for {
        productList <- products.filter(_.id.inSet(productIds)).result
        inserted <- (orders.map(o => (o.userId, o.addressId, o.totalPrice, o.status, o.createdAt, o.updatedAt)) returning orders
          .map(_.id) into (
          (data, id) => Order(id, data._1, data._2, data._3, data._4, data._5, data._6)
          )) += (order.userId, order.addressId, productList.map(_.salePrice).sum, 1, now, now)
        _ <- DBIO.seq(
          orderProducts ++= order.products.map(i =>
            OrderProduct(inserted.id, Some(i.id), i.quantity, i.size, productList.filter(_.id.get == i.id).head.salePrice)
          )
        )
      } yield inserted
    }
  }

  def update(id: Long, status: Int): Future[Int] = {
    val action = orders
      .filter(_.id === id)
      .map(o => (o.status, o.updatedAt))
      .update((status, new Timestamp(System.currentTimeMillis())))

    db.run(action)
  }

  def cancelOrder(id: Long): Future[Int] = {
    val action = orders
      .filter(_.id === id)
      .map(o => (o.status, o.updatedAt))
      .update((1, new Timestamp(System.currentTimeMillis())))

    db.run(action)
  }

  def delete(id: Long): Future[Int] = {
    db.run {
      orders.filter(_.id === id).delete
    }
  }
}