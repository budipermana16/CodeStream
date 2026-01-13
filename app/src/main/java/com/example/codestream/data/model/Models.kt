package com.example.codestream.data.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String = "",
    val bio: String = ""
)
data class Course(val id: Long, val title: String, val description: String, val price: Int, val category: String)
data class Lesson(
    val id: Long,
    val courseId: Long,
    val title: String,
    val type: String,
    val contentUrl: String,
    val orderIndex: Int,
    val isDone: Boolean
)
data class Certificate(
    val id: Long,
    val courseId: Long,
    val courseTitle: String,
    val issuedAt: String
)
