package com.anilsenay.schema

import com.anilsenay.models.Order
import com.anilsenay.utils.SelectedProfile.api._
import com.anilsenay.schema.UsersTable.Users
import com.anilsenay.schema.AddressTable.Addresses

import java.sql.Timestamp

object OrderTable {

  class Orders(tag: Tag) extends Table[Order](tag, "orders") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Option[Long]]("user_id")
    def addressId = column[Option[Long]]("address_id")
    def totalPrice = column[Double]("total_price")
    def status = column[Int]("order_status", O.Default(0))
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))

    def * =
      (id, userId, addressId, totalPrice, status, createdAt, updatedAt) <> ((Order.apply _).tupled, Order.unapply)

    def user = foreignKey("fk_user", userId, TableQuery[Users])(_.id)
    def address = foreignKey("fk_address", addressId, TableQuery[Addresses])(_.id)

  }

  val orders = TableQuery[Orders]

}
