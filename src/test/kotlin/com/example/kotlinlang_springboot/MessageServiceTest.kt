package com.example.kotlinlang_springboot

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals

internal class MessageServiceTest {
    @Mock
    private lateinit var mockDb: MessageRepository

    private lateinit var mockService: MessageService


    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        mockService = MessageService(mockDb)
    }


    @Test
    fun `retrieve all messages (list not empty)`() {
        // arrange
        val messageList = listOf<Message>(
            Message(text = "Message 1 text", id = "0000"),
            Message(text = "Message 2 text", id = "0001"),
        )
        Mockito.`when`(mockDb.findAll()).thenReturn(messageList)

        // act
        val actual = mockService.findMessages()

        // assert
        assertEquals(expected = messageList, actual = actual)
    }

    @Test
    fun `retrieve all messages (list  empty)`() {
        // arrange
        val mockMessageList = listOf<Message>()
        Mockito.`when`(mockDb.findAll()).thenReturn(mockMessageList)

        // act
        val actual = mockService.findMessages()

        // assert
        assertEquals(expected = mockMessageList, actual = actual)
    }

    @Test
    fun `find message by valid ID`() {
        // arrange
        val mockMessageId = "Message ID"
        val mockMessage = Message(text = "Message text",id = mockMessageId)
        Mockito.`when`(mockDb.findByIdOrNull(mockMessageId)).thenReturn(mockMessage)

        // act
        val actual = mockService.findMessageById(mockMessageId)

        // assert
        assertEquals(expected = mockMessage, actual = actual)
    }

    @Test
    fun `find message by invalid ID`() {
        // arrange
        val mockMessageID = "non-existent ID"
        Mockito.`when`(mockDb.findByIdOrNull(mockMessageID)).thenReturn(null)

        // act
        val actual = mockService.findMessageById(mockMessageID)

        // assert
        assertEquals(expected = null, actual = actual)
    }

    @Test
    fun `save a message with ID`() {
        // arrange
        val mockMessage = Message(text = "Message text",id = "Message ID")
        Mockito.`when`(mockDb.save(mockMessage)).thenReturn(mockMessage)

        // act
        val actual = mockService.save(mockMessage)

        // assert
        assertEquals(expected = mockMessage, actual = actual)
    }


    @Test
    fun `save a message, failed 5 times error`() {
        // arrange
        val mockMessage = Message(text = "Message text")
        Mockito.`when`(mockDb.save(mockMessage)).thenThrow(RuntimeException("Failed to save message after 5 attempts"))

        // act
        val actual = assertThrows<RuntimeException> {
            mockService.save(mockMessage)
        }

        // assert
        assertEquals(expected = ("Failed to save message after 5 attempts"),actual= actual.message)
        // assertThrows<RuntimeException> { mockService.save(mockMessage) } or we could just do this instead
    }

    @Test
    fun `save a message without ID`() {
        // arrange
        val mockMessageText =  "Message text"
        val mockMessageWithoutID = Message(mockMessageText)
        val mockMessageWithRandomID = Message(mockMessageText, "randomID")
        Mockito.`when`(mockDb.save(Message(mockMessageText))).thenReturn(mockMessageWithRandomID)


        // act
        val actual = mockService.save(mockMessageWithoutID)

        // assert
        assertEquals(expected = mockMessageWithRandomID, actual = actual)
    }

    @Test
    fun `save an empty message`() {
        // arrange
        val mockMessageText =  ""
        val mockMessage = Message(mockMessageText, "Message ID")
        Mockito.`when`(mockDb.save(Message(mockMessageText))).thenReturn(mockMessage)

        // act
        val actual = mockService.save(mockMessage)

        // assert
        assertEquals(expected = mockMessage.text, actual = actual.text)
    }


    @Test
    fun `save and retrieve a message`(){
        // arrange
        val mockMessageId = "Message ID"
        val mockMessage = Message(text = "Message text",id = mockMessageId)
        Mockito.`when`(mockDb.save(mockMessage)).thenReturn(mockMessage)
        Mockito.`when`(mockDb.findByIdOrNull(mockMessageId)).thenReturn(mockMessage)

        // act
        val savedMessage = mockService.save(mockMessage)
        val retrievedMessage = mockService.findMessageById(mockMessageId)

        // assert
        assertEquals(mockMessage, savedMessage)
        assertEquals(mockMessage, retrievedMessage)
    }
}
