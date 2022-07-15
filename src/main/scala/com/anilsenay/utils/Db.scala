package com.anilsenay.utils

import slick.jdbc.{JdbcProfile, MySQLProfile, PostgresProfile}

/* Database Profile Types */
object DatabaseProfile {
 val postgresProfile = PostgresProfile
 val mySQLProfile = MySQLProfile
}

/* Select Profile Type Here */
object SelectedProfile {
 val profile = DatabaseProfile.postgresProfile
 val api: profile.API = profile.api
}

/* Generic Database Layer */
trait Profile {
 val profile: JdbcProfile
}

trait DatabaseModule1 { self: Profile =>
 import profile.api._
 val db: profile.backend.DatabaseFactoryDef = Database
}

class DatabaseLayer(val profile: JdbcProfile) extends Profile with DatabaseModule1

/* Database Config Objects */
object PostgresDb {
 val profile = SelectedProfile.profile
 val db = new DatabaseLayer(profile).db.forConfig("postgresDb")
}

object MysqlDb {
 val profile = SelectedProfile.profile
 val db = new DatabaseLayer(profile).db.forConfig("mysqlDb")
}
