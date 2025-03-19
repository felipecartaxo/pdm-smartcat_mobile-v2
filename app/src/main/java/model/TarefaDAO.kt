package com.application.smartcat.model

import com.application.smartcat.util.Sessao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class TarefaDAO {
    private val db = FirebaseFirestore.getInstance()

    fun buscar(callback: (List<Tarefa>) -> Unit) {
        val usuarioAtual = Sessao.usuarioAtual
        // Verifica se o usuário está logado
        if (usuarioAtual == null) {
            callback(emptyList())
            return
        }
        // Adiciona o id do usuario ao objeto tarefa antes de adicioná-lo ao Firestore
        db.collection("tarefas")
            .whereEqualTo("usuarioId", usuarioAtual.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tarefas = querySnapshot.documents.map { doc ->
                    val dataTimestamp = doc.get("data") as? Timestamp
                    Tarefa(
                        id = doc.id,
                        titulo = doc.getString("titulo") ?: "",
                        descricao = doc.getString("descricao") ?: "",
                        data = dataTimestamp,
                        usuarioId = doc.getString("usuarioId") ?: "",
                        status = (doc.get("status") as? Long)?.toInt() ?: 1 // Incluindo status
                    )
                }
                callback(tarefas)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorId(id: String, callback: (Tarefa?) -> Unit) {
        db.collection("tarefas").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val dataTimestamp = doc.get("data") as? Timestamp
                    val tarefa = Tarefa(
                        id = doc.id,
                        titulo = doc.getString("titulo") ?: "",
                        descricao = doc.getString("descricao") ?: "",
                        data = dataTimestamp,
                        usuarioId = doc.getString("usuarioId") ?: "",
                        status = (doc.get("status") as? Long)?.toInt() ?: 1 // Incluindo status
                    )
                    callback(tarefa)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun adicionar(tarefa: Tarefa, callback: (Boolean) -> Unit) {
        val usuarioAtual = Sessao.usuarioAtual
        if (usuarioAtual == null) {
            callback(false)
            return
        }
        val tarefaComUsuario = tarefa.copy(usuarioId = usuarioAtual.id, status = 1) // Inicia com status 1
        db.collection("tarefas").add(tarefaComUsuario)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun alterar(id: String, tarefa: Tarefa, callback: (Boolean) -> Unit) {
        val usuarioAtual = Sessao.usuarioAtual
        if (usuarioAtual == null) {
            callback(false)
            return
        }
        val tarefaComUsuario = tarefa.copy(usuarioId = usuarioAtual.id)
        db.collection("tarefas").document(id).set(tarefaComUsuario)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun moverTarefa(id: String, novoStatus: Int, callback: (Boolean) -> Unit) {
        db.collection("tarefas").document(id).update("status", novoStatus)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun remover(id: String, callback: (Boolean) -> Unit) {
        db.collection("tarefas").document(id).delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}