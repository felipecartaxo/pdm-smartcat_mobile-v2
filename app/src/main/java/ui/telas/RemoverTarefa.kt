package com.application.smartcat.ui.telas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import com.application.smartcat.model.TarefaDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RemoverTarefa(tarefaId: String, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tarefaDAO = remember { TarefaDAO() }
    var isDeleting by remember { mutableStateOf(false) }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Lógica para exibir notificação após remover uma tarefa
    fun exibirNotificacaoRemocao() {
        val channelId = "remocao_tarefa"

        // Criar canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Remoção de Tarefas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações de tarefas removidas"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Construir notificação
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_delete) // Ícone de lixeira
            .setContentTitle("Tarefa removida!")
            .setContentText("Tarefa removida com sucesso!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(500, 500))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        title = { Text("Remover Tarefa") },
        text = { Text("Se remover esta tarefa, não será possível recuperá-la. Deseja continuar?") },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    isDeleting = true
                    scope.launch(Dispatchers.IO) {
                        tarefaDAO.remover(tarefaId) { sucesso ->
                            isDeleting = false
                            if (sucesso) {
                                exibirNotificacaoRemocao()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Erro ao remover a tarefa.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }, enabled = !isDeleting) {
                    Text("Remover")
                }
                Button(onClick = { navController.popBackStack() }) {
                    Text("Cancelar")
                }
            }
        }
    )
}