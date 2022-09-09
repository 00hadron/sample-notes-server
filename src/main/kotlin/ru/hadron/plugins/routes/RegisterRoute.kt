package ru.hadron.plugins.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.hadron.data.checkIfUserExist
import ru.hadron.data.collections.User
import ru.hadron.data.registerUser
import ru.hadron.data.requests.AccountRequest
import ru.hadron.data.responses.SimpleResponse

fun Route.registerRoute() {
    route("/register") {
        post {
            val request = try {
            call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }
            val userExist = checkIfUserExist(request.email)
            if (!userExist) {
                if (registerUser(User(request.email, request.password))) {
                    call.respond(OK, SimpleResponse(true, "Account created. Success!"))
                } else {
                    call.respond(OK, SimpleResponse(false, "An unknown error :("))
                }
            } else {
                call.respond(OK, SimpleResponse(false, "User already exist!" ))
            }
        }
    }
}