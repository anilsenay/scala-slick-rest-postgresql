package com.anilsenay.services

import com.anilsenay.models.{Address, UserAddress}
import com.anilsenay.schema.AddressTable._
import com.anilsenay.schema.UserAddressTable.userAddresses
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.Future

object AddressService extends BaseService {

  def getAllAddress: Future[Seq[Address]] = db.run(addresses.result)

  def getAddress(id: String): Future[Option[Address]] = {
    db.run {
      addresses.filter(_.id === id).result.headOption
    }
  }

  def insertAddress(title: String, city: String, region: String, zipcode: String, fullAddress: String): Future[Address] = {
    val action = (addresses.map(address => (address.title, address.city, address.region, address.zipcode, address.fullAddress)) returning addresses
      .map(_.id) into (
      (addressData,
       id) => Address(id, addressData._1, addressData._2, addressData._3, addressData._4, addressData._5)
      )) += (title, city, region, zipcode, fullAddress)
    db.run(action)
  }

  def insertUserAddress(userId: String, title: String, city: String, region: String, zipcode: String, fullAddress: String): Future[Int] = {
    val action = (addresses.map(address => (address.title, address.city, address.region, address.zipcode, address.fullAddress)) returning addresses
      .map(_.id) into (
      (addressData,
       id) => Address(id, addressData._1, addressData._2, addressData._3, addressData._4, addressData._5)
      )) += (title, city, region, zipcode, fullAddress)
    db.run(action) flatMap { address =>
      db.run(userAddresses += UserAddress(address.id, Some(userId)))
    }
  }

  def update(id: String, address: Address): Future[Int] = {
    val action = addresses
      .filter(_.id === id)
      .map(a => (a.title, a.city, a.region, a.zipcode, a.fullAddress))
      .update((address.title, address.city, address.region, address.zipcode, address.fullAddress))

    db.run(action)
  }

  def delete(id: String): Future[Int] = {
    db.run {
      addresses.filter(_.id === id).delete
    }
  }
}