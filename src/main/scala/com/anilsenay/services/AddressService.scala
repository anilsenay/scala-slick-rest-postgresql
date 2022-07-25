package com.anilsenay.services

import com.anilsenay.models.{Address, UserAddress}
import com.anilsenay.schema.AddressTable._
import com.anilsenay.schema.UserAddressTable.userAddresses
import com.anilsenay.utils.SelectedProfile.api._

import scala.concurrent.Future

class AddressService {

}

object AddressService extends BaseService {

  def getAllAddress: Future[Seq[Address]] = db.run(addresses.result)

  def getAddress(id: Long): Future[Option[Address]] = {
    db.run {
      addresses.filter(_.id === id).result.headOption
    }
  }

  def findAddressByUserId(addressId: Long, userId: Long): Future[Option[UserAddress]] = {
    db.run {
      userAddresses.filter(_.addressId === addressId).filter(_.userId === userId).result.headOption
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

  def insertUserAddress(userId: Long, title: String, city: String, region: String, zipcode: String, fullAddress: String): Future[Int] = {
    val action = (addresses.map(address => (address.title, address.city, address.region, address.zipcode, address.fullAddress)) returning addresses
      .map(_.id) into (
      (addressData,
       id) => Address(id, addressData._1, addressData._2, addressData._3, addressData._4, addressData._5)
      )) += (title, city, region, zipcode, fullAddress)
    db.run(action) flatMap { address =>
      db.run(userAddresses += UserAddress(Some(userId), address.id))
    }
  }

  def update(id: Long, address: Address): Future[Int] = {
    db.run(addresses
      .filter(_.id === id)
      .map(a => (a.title, a.city, a.region, a.zipcode, a.fullAddress))
      .update((address.title, address.city, address.region, address.zipcode, address.fullAddress)))
  }

  def delete(id: Long): Future[Int] = {
    db.run {
      addresses.filter(_.id === id).delete
    }
  }
}