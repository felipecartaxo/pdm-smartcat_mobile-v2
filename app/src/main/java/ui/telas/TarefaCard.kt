package com.application.smartcat.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.application.smartcat.model.Tarefa
import com.application.smartcat.util.formatInstant
import kotlinx.datetime.Instant

@Composable
fun TarefaCard(
    tarefa: Tarefa,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChangeStatus: (Int) -> Unit
) {
    val statusTexto = if (tarefa.status == 1) "A Fazer" else "Concluído"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD0CFEA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Linha superior (Título + ícones de edição e exclusão)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Título: ${tarefa.titulo}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar tarefa"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remover tarefa"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Descrição: ${tarefa.descricao}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Status: $statusTexto", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Data: ${
                    if (tarefa.data != null) formatInstant(
                        Instant.fromEpochSeconds(tarefa.data.seconds, tarefa.data.nanoseconds)
                    ) else "Sem data"
                }",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ícone de Check para Concluir Tarefa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onChangeStatus(2) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (tarefa.status == 1) Icons.Outlined.CheckCircle else Icons.Filled.CheckCircle,
                        contentDescription = "Marcar como concluído",
                        tint = if (tarefa.status == 1) Color.Gray else Color(0xFF8E8CCC) // A cor pode ser diferente quando concluído
                    )

                }
            }
        }
    }
}