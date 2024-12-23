package ru.aston.recyclercontacts

data class Contact(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    var isSelected: Boolean = false
)