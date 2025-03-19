package com.application.smartcat.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp

data class Tarefa(
    @DocumentId val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val data: Timestamp? = null,
    val usuarioId: String = "",
    val status: Int = 1 // 1 = A Fazer, 2 = Concluído
)