package com.application.smartcat.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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