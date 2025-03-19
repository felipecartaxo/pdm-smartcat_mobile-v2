package com.application.smartcat.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.smartcat.model.Tarefa
import com.application.smartcat.model.TarefaDAO
import com.application.smartcat.util.formatInstant
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPrincipal(navController: NavController, onLogoffClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val tarefaDAO = remember { TarefaDAO() }
    var tarefas by remember { mutableStateOf<List<Tarefa>>(emptyList()) }
    var barraDePesquisa by remember { mutableStateOf("") }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Busca apenas as tarefas do status "A Fazer" (1)
    LaunchedEffect(tarefaDAO) {
        tarefaDAO.buscar { lista ->
            tarefas = lista.filter { it.status == 1 } // Apenas tarefas com status "A Fazer"
        }
    }

    // Filtra por título, descrição ou data
    val tarefasFiltradas = tarefas.filter { tarefa ->
        barraDePesquisa.isBlank() ||
                tarefa.titulo.contains(barraDePesquisa, true) ||
                tarefa.descricao.contains(barraDePesquisa, true) ||
                (tarefa.data != null && formatInstant(
                    Instant.fromEpochSeconds(tarefa.data.seconds, tarefa.data.nanoseconds)
                ).contains(barraDePesquisa, true))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8E8CCC))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bem-vindo(a) de volta!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 140.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = barraDePesquisa,
                onValueChange = { barraDePesquisa = it },
                placeholder = { Text("Filtre por título, descrição ou data", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD0CFEA),
                    unfocusedContainerColor = Color(0xFFD0CFEA),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            Text(
                text = "Suas próximas tarefas:",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tarefasFiltradas) { tarefa ->
                    TarefaCard(
                        tarefa = tarefa,
                        onEdit = { navController.navigate("editarTarefa/${tarefa.id}") },
                        onDelete = { navController.navigate("removerTarefa/${tarefa.id}") },
                        onChangeStatus = {}
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("listagem") },
                containerColor = Color(0xFFD0CFEA)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Listar Tarefas")
            }
            FloatingActionButton(
                onClick = { navController.navigate("cadastroTarefa") },
                containerColor = Color(0xFFD0CFEA)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    }
}