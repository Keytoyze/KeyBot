package indi.key.keybot.util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*
import kotlin.concurrent.thread

@Synchronized
fun Connection.executeUpdateSQL(statement: String, placeHolder: Array<Any> = emptyArray()) {
    synchronized(this) {
        println("Execute: $statement, placeHolder: ${Arrays.toString(placeHolder)}")
        val preparedStatement = prepareStatement(statement)
        placeHolder.forEachIndexed { index, param ->
            when (param) {
                is Long -> preparedStatement.setLong(index + 1, param)
                is String -> preparedStatement.setString(index + 1, param)
                else -> error("Unknown type: ${param.javaClass.name}")
            }
        }
        val result = preparedStatement.executeUpdate()
        println("Result: $result")
    }
}

private fun <T> Connection.realExecuteQuerySQL(statement: String, mapper: (ResultSet) -> T): List<T> {
    println("Execute: $statement")
    val resultSet = prepareStatement(statement).executeQuery()
    val result = mutableListOf<T>()
    while (resultSet != null && resultSet.next()) {
        result.add(mapper(resultSet))
    }
    println("Result: $result")
    return result
}

fun <T> Connection.executeQuerySQL(statement: String, mapper: (ResultSet) -> T): List<T> {
    return synchronized(this) {
        realExecuteQuerySQL(statement, mapper)
    }
}

fun initializeDB(path: String): Connection {
    println("Initialize DB $path start")
    Class.forName("org.sqlite.JDBC")
    val connection = DriverManager.getConnection("jdbc:sqlite:$path")
    Runtime.getRuntime().addShutdownHook(
        thread(start = false) {
            println("Close DB $path start")
            connection.close()
            println("Close DB $path success")
        }
    )
    println("Initialize DB $path end")
    return connection
}