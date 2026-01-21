package com.example.kotlinlang_springboot


import org.springframework.stereotype.Service
import org.springframework.data.repository.findByIdOrNull

@Service
class MessageService(private val db: MessageRepository) {
    fun findMessages(): List<Message> = db.findAll().toList()

    fun findMessageById(id: String): Message? = db.findByIdOrNull(id)

    fun save(message: Message, attempts: Int = 5): Message {
        //refactored to use a failsafe approach instead of volatile raw RANDOM_UUID
        return try {
            db.save(message)
        } catch (e: Exception) {
            if (attempts > 0) {
                val newAttemptsNumber = attempts-1
                save(message = message, attempts = newAttemptsNumber)
            } else {
                throw RuntimeException("Failed to save message after 5 attempts", e)
            }
        }
        }
    }