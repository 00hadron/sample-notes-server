package ru.hadron.plugins.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.hadron.data.*
import ru.hadron.data.collections.Note
import ru.hadron.data.requests.AddOwnerRequest
import ru.hadron.data.requests.DeleteNoteRequest
import ru.hadron.data.responses.SimpleResponse

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val notes = getNotesForUser(email)
                call.respond(OK, notes)
            }
        }
    }

    route("/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: io.ktor.server.plugins.ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (saveNote(note)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (deleteNoteForUser(email, request.id)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/addOwnerToNote") {
        authenticate {
            post {
                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (!checkIfUserExist(request.owner)) {
                    call.respond(OK, SimpleResponse(false, "No user with this email"))
                    return@post
                }
                if (isOwnerOfNote(request.noteId, request.owner)) {
                    call.respond(OK, SimpleResponse(false, "A user is already an owner of this note"))
                    return@post
                }
                if (addOwnerToNote(request.noteId, request.owner)) {
                    call.respond(OK, SimpleResponse(true, "${request.owner} is able to see this note"))
                } else
                    call.respond(Conflict)
            }
        }
    }
}