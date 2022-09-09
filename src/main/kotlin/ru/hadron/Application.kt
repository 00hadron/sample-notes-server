package ru.hadron

import io.ktor.client.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hadron.data.collections.User
import ru.hadron.data.registerUser

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    CoroutineScope(Dispatchers.IO).launch {
        registerUser(
            User(
                "hadron@yandex.ru",
                "123456"
            )
        )
    }
}