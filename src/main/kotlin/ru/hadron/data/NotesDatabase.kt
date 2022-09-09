package ru.hadron.data

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import ru.hadron.data.collections.Note
import ru.hadron.data.collections.User

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("NotesDatabase")
private val notes = database.getCollection<Note>()
private val users = database.getCollection<User>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExist(email: String): Boolean {
   // return users.findOne("{email: $email}") != null
    return users.findOne(User::email eq email) != null
}