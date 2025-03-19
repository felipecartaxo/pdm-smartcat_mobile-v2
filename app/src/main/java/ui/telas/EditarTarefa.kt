package com.application.smartcat.ui.telas

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.application.smartcat.model.Tarefa
import com.application.smartcat.model.TarefaDAO
import com.application.smartcat.util.formatInstant
import com.application.smartcat.util.parseDate
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTarefa(
    navController: NavController,
    tarefaId: String,
    onSalvar: (Tarefa) -> Unit // Adicionando a função onSalvar como parâmetro
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tarefaDAO = TarefaDAO()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var dataSelecionada by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(1) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) } // Adicionando a variável expanded

    val statusOpcoes = listOf("A Fazer",  "Concluído")
    val statusValores = listOf(1, 2) // definindo o status das tarefas criadas

    LaunchedEffect(tarefaId) {
        tarefaDAO.buscarPorId(tarefaId) { tarefa ->
            if (tarefa != null) {
                titulo = tarefa.titulo
                descricao = tarefa.descricao
                dataSelecionada = tarefa.data?.let { formatInstant(Instant.fromEpochSeconds(it.seconds, it.nanoseconds)) } ?: ""
                status = tarefa.status
            }
        }
    }

    val now = Clock.System.now()
    val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

    Dialog(onDismissRequest = { navController.popBackStack() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Editar Tarefa",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.width(280.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.width(280.dp)
                )

                // Aba de status adicionada com um select dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = statusOpcoes[status - 1], // Mostra as ocpoes correspondentes
                        onValueChange = {},
                        label = { Text("Status") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menu")
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOpcoes.forEachIndexed { index, texto ->
                            DropdownMenuItem(
                                text = { Text(texto) },
                                onClick = {
                                    status = statusValores[index] // Atualiza o status (assimilando ao index escolhido)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val localDateTimePicker = now.toLocalDateTime(TimeZone.currentSystemDefault())
                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val dayStr = dayOfMonth.toString().padStart(2, '0')
                                val monthStr = (month + 1).toString().padStart(2, '0')
                                dataSelecionada = "$dayStr/$monthStr/$year"
                            },
                            localDateTimePicker.year,
                            localDateTimePicker.monthNumber - 1,
                            localDateTimePicker.dayOfMonth
                        )
                        datePickerDialog.show()
                    },
                    modifier = Modifier.width(280.dp)
                ) {
                    Text(if (dataSelecionada.isNotEmpty()) "Data: $dataSelecionada" else "Selecionar Data")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if (titulo.isNotEmpty() && descricao.isNotEmpty() && dataSelecionada.isNotEmpty()) {
                            val instantData = parseDate(dataSelecionada)
                            if (instantData == null) {
                                mensagemErro = "Formato de data inválido."
                            } else {
                                val dataSelecionadaLocal = instantData.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                if (dataSelecionadaLocal < localDateTime) {
                                    mensagemErro = "A data não pode ser menor que a atual."
                                } else {
                                    val timestampData = Timestamp(instantData.epochSeconds, 0)
                                    val tarefaAtualizada = Tarefa(
                                        id = tarefaId,
                                        titulo = titulo,
                                        descricao = descricao,
                                        data = timestampData,
                                        status = status
                                    )
                                    scope.launch(Dispatchers.IO) {
                                        tarefaDAO.alterar(tarefaId, tarefaAtualizada) { sucesso ->
                                            if (sucesso) navController.popBackStack()
                                        }
                                    }
                                }
                            }
                        } else {
                            mensagemErro = "Todos os campos são obrigatórios."
                        }
                    }) {
                        Text("Salvar")
                    }

                    Button(onClick = { navController.popBackStack() }) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }

    mensagemErro?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            mensagemErro = null
        }
    }
}
