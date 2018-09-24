package net.bmac.hitcounter

import com.google.gson.Gson
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

val gson = Gson()

@RestController
class RestController {
    @Autowired
    lateinit var db: Db
    @PostMapping("/hit")
    fun hit(req : HttpServletRequest) {
        val headerNames = req.headerNames
        val headerMap = headerNames.toList().associateBy({it}, {req.getHeader(it)})
        transaction {
            Hits.insert {
                it[ip] = req.remoteAddr
                it[headers] = gson.toJson(headerMap)
                it[date] = DateTime.now()
            }
        }
    }

    @GetMapping("/list")
    fun get(): String {
        return transaction {
            "Count: " + Hits.selectAll().count().toString() + "\n" +
            Hits.selectAll().map ({"Host: " + it[Hits.ip] + " Date: " + it[Hits.date].toString() + " Headers: " + it[Hits.headers]}).joinToString("\n")
        }
    }
}