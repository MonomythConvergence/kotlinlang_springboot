package com.example.kotlinlang_springboot

import org.springframework.data.repository.CrudRepository

interface MessageRepository : CrudRepository<Message, String>