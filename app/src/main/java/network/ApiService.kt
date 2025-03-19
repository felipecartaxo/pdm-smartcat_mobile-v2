package com.application.smartcat.network

import com.application.smartcat.model.api.Mensagem
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("mensagem") // Endereço da sua API
    fun getMensagens(): Call<List<Mensagem>> // Retorna uma lista de mensagens
}