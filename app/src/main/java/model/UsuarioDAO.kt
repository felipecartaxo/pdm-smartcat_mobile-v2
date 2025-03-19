package com.application.smartcat.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class UsuarioDAO {
    private val db = FirebaseFirestore.getInstance()

    fun buscar(callback: (List<Usuario>) -> Unit) {
        db.collection("usuarios").get()
            .addOnSuccessListener { document ->
                val usuarios = document.toObjects<Usuario>()
                callback(usuarios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorNome(nome: String, callback: (Usuario?) -> Unit) {
        // A consulta retorna o documento que possui o campo 'nome' igual ao informado
        db.collection("usuarios").whereEqualTo("nome", nome).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val usuario = document.documents[0].toObject<Usuario>()
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorId(id: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").document(id).get()
            .addOnSuccessListener { documento ->
                callback(documento.takeIf { it.exists() }?.toObject<Usuario>())
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun adicionar(usuario: Usuario, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").add(usuario)
            .addOnSuccessListener { documentoRef ->
                documentoRef.get()
                    .addOnSuccessListener { documento ->
                        val usuarioAdicionado = documento.toObject<Usuario>()
                        callback(usuarioAdicionado)
                    }
                    .addOnFailureListener {
                        callback(null)
                    }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}