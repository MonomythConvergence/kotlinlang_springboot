package com.example.kotlinlang_springboot

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import java.net.URI
import kotlin.test.assertEquals

class MessageControllerTests {

    @Mock
    private lateinit var mockService: MessageService
    private lateinit var mockController: MessageController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        mockController = MessageController(mockService)
    }

    @Test
    fun `retrieve all messages (list not empty)`() {
        // arrange
        val messageList = listOf(
            Message(text = "Message 1 text", id = "0000"),
            Message(text = "Message 2 text", id = "0001"),
        )
        Mockito.`when`(mockService.findMessages()).thenReturn(messageList)

        // act
        val actual = mockController.listMessages()

        // assert
        assertEquals(expected = HttpStatus.OK, actual = actual.statusCode)
        assertEquals(expected = messageList, actual = actual.body)
    }

    @Test
    fun `retrieve all messages (list empty)`() {
        // arrange
        val messageList = emptyList<Message>(
        )
        Mockito.`when`(mockService.findMessages()).thenReturn(messageList)

        // act
        val actual = mockController.listMessages()

        // assert
        assertEquals(expected = HttpStatus.OK, actual = actual.statusCode)
        assertEquals(expected = messageList, actual = actual.body)
    }

    @Test
    fun `post message with an id`() {
        // arrange
        val message = Message(text = "Message 1 text", id = "0000")
        Mockito.`when`(mockService.save(message)).thenReturn(message)

        // act
        val actual = mockController.post(message)

        // assert
        assertAll(
            { assertEquals(HttpStatus.CREATED, actual.statusCode) },
            { assertEquals(message, actual.body) },
            { assertEquals(URI("/${message.id}"), actual.headers.location) }
        )
    }

    @Test
    fun `post message without id`() {
        // arrange
        val inputMessage = Message(text = "Message 1 text")
        val expectedOutput = Message(text = "Message 1 text", id = "0000")
        Mockito.`when`(mockService.save(inputMessage)).thenReturn(expectedOutput)

        // act
        val actual = mockController.post(inputMessage)

        // assert
        assertAll(
            { assertEquals(HttpStatus.CREATED, actual.statusCode) },
            { assertEquals(expectedOutput, actual.body) },
            { assertEquals(URI("/${expectedOutput.id}"), actual.headers.location) }
        )
    }

    @Test
    fun `post an empty message `() {
        // arrange
        val message = Message(text = "")
        Mockito.`when`(mockService.save(message)).thenReturn(message)

        // act
        val actual = mockController.post(message)

        // assert
        assertAll(
            { assertEquals(HttpStatus.CREATED, actual.statusCode) },
            { assertEquals(message, actual.body) },
            { assertEquals(URI("/${message.id}"), actual.headers.location) }
        )
    }

    @Test
    fun `save and retrieve a message`() {
        // arrange
        val messageId = "0000"
        val message = Message(text = "Message 1 text", id = messageId)
        Mockito.`when`(mockService.save(message)).thenReturn(message)
        Mockito.`when`(mockService.findMessageById(messageId)).thenReturn(message)

        // act
        val postResponse = mockController.post(message)
        val getResponse = mockController.getMessage(messageId)

        // assert
        assertAll(
            { assertEquals(HttpStatus.CREATED, postResponse.statusCode) },
            { assertEquals(HttpStatus.OK, getResponse.statusCode) },
            { assertEquals(message, postResponse.body) },
            { assertEquals(message, getResponse.body) },
            { assertEquals(URI("/${message.id}"), postResponse.headers.location) }
        )
        //also checks that toResponseEntity() works correctly on non-null
    }

    @Test
    fun `attempt get retrieve a non-existent message ID`() {
        // arrange
        val messageID = "non-existent ID"
        Mockito.`when`(mockService.findMessageById(messageID)).thenReturn(null)

        // act
        val actual = mockController.getMessage(messageID)

        // assert
        assertEquals(HttpStatus.NOT_FOUND, actual.statusCode)
    }
    //effectively checks toResponseEntity's elvis
}








