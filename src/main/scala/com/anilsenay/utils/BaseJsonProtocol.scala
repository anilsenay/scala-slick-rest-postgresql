package com.anilsenay.utils

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonFormat, RootJsonFormat}

trait BaseJsonProtocol extends DefaultJsonProtocol {
  implicit val timestampFormat: JsonFormat[Timestamp] = new JsonFormat[Timestamp] {
    override def write(obj: Timestamp): JsValue = JsNumber(obj.getTime)

    override def read(json: JsValue): Timestamp = json match {
      case JsNumber(x) => Timestamp.from(Instant.ofEpochMilli(x.toLong))
      case _ =>
        throw new IllegalArgumentException(
          s"Can not parse json value [$json] to a timestamp object")
    }
  }

  implicit val uuidJsonFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    override def write(x: UUID): JsValue = JsString(x.toString)

    override def read(value: JsValue): UUID = value match {
      case JsString(x) => UUID.fromString(x)
      case x =>
        throw new IllegalArgumentException("Expected UUID as JsString, but got " + x.getClass)
    }
  }
}