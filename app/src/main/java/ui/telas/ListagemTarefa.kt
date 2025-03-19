package com.application.smartcat.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.application.smartcat.model.Tarefa
import com.application.smartcat.model.TarefaDAO
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@Composable
fun ListagemTarefa(navController: NavController) {
    val scope = rememberCoroutineScope()
    val tarefaDAO = remember { TarefaDAO() }
    var tarefas by remember { mutableStateOf<List<Tarefa>>(emptyList()) }
    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) { tarefaDAO.buscar { tarefas = it } }

    val categorias = listOf("A Fazer", "Concluído")
    val statusMap = mapOf("A Fazer" to 1, "Concluído" to 2)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF8E8CCC))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Categoria: ${categorias[pagerState.currentPage]}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(50.dp)
                    .background(
                        color = Color(0xFFB39DDB),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            TabRow(selectedTabIndex = pagerState.currentPage) {
                categorias.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(count = categorias.size, state = pagerState) { page ->
                val status = statusMap[categorias[page]] ?: 1
                val tarefasFiltradas = tarefas.filter { it.status == status }

                Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(tarefasFiltradas, key = { it.id }) { tarefa ->
                            TarefaCard(
                                tarefa = tarefa,
                                onEdit = { navController.navigate("editarTarefa/${tarefa.id}") },
                                onDelete = { navController.navigate("removerTarefa/${tarefa.id}") },
                                onChangeStatus = { novoStatus ->
                                    tarefaDAO.moverTarefa(tarefa.id, novoStatus) { sucesso ->
                                        if (sucesso) {
                                            tarefaDAO.buscar { tarefas = it }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFFD0CFEA)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
        }
    }
}