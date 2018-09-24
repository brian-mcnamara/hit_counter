package net.bmac.hitcounter

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import javax.annotation.PostConstruct

object Hits : IntIdTable() {
    var ip = varchar("ip", 15)
    var date = date("date")
    var headers = text("headers")
}

@Component
class Db {

    @Value("\${DATABASE_URL}")
    lateinit var db_url: String

    @PostConstruct
    fun init() {
        val uri = URI.create(db_url)
        val userStr = uri.userInfo.split(":")
        Database.Companion.connect("jdbc:postgresql://" + uri.host + ":" + uri.port + uri.path, "org.postgresql.Driver", userStr[0], userStr[1])
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Hits)
        }
    }
}