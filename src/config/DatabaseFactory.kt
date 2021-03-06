package config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import schemas.Teams
import java.util.*

object DatabaseFactory {
    lateinit var database: Database
    fun init(dotenv: Dotenv) {
        database = Database.Companion.connect(config(dotenv))

        transaction {
            SchemaUtils.create(Teams)
        }
    }

    private fun config(dotenv: Dotenv): HikariDataSource {
        val props = Properties()
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
        setProperty(props, "user", dotenv["USER"])
        setProperty(props, "password", dotenv["PASSWORD"])
        setProperty(props, "databaseName", dotenv["DATABASE"])
        setProperty(props, "portNumber", dotenv["PORT_DB"])
        setProperty(props, "serverName", dotenv["SERVER"])

        val config = HikariConfig(props)
        config.validate()

        return HikariDataSource(config)
    }

    private fun setProperty(props: Properties, property: String, value: String) {
        props.setProperty("dataSource.$property", value)
    }
}
