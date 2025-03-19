package com.application.smartcat.model

import com.google.firebase.firestore.DocumentId

data class Usuario(
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val senha: String = ""
)