package com.application.smartcat

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.smartcat.ui.telas.*
import com.application.smartcat.ui.theme.SmartCatTheme
import com.application.smartcat.util.Sessao
import com.google.firebase.FirebaseApp
import com.application.smartcat.ui.theme.AppBackground
import android.Manifest


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permissão para o sistema Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(this, "Notificações desativadas", Toast.LENGTH_SHORT).show()
                }
            }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            SmartCatTheme {
                val navController: NavHostController = rememberNavController()
                var usuarioLogado by remember { mutableStateOf(Sessao.usuarioAtual != null) }

                Scaffold(
                    topBar = {
                        AppTopBar(
                            usuarioLogado = usuarioLogado,
                            onLogoffClick = {
                                Sessao.usuarioAtual = null
                                usuarioLogado = false
                                navController.navigate("login") {
                                    popUpTo("principal") { inclusive = true }
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            TelaLogin(
                                modifier = Modifier.padding(innerPadding),
                                onLogarClick = {
                                    Sessao.usuarioAtual?.let {
                                        usuarioLogado = true
                                        navController.navigate("principal")
                                    }
                                },
                                onCadastroClick = { navController.navigate("cadastro") }
                            )
                        }
                        composable("cadastro") {
                            TelaCadastro(
                                modifier = Modifier.padding(innerPadding),
                                onCadastroSucesso = { navController.navigate("login") },
                                onCancelar = { navController.navigate("login") }
                            )
                        }
                        composable("principal") {
                            Sessao.usuarioAtual?.let {
                                usuarioLogado = true
                                TelaPrincipal(navController) {
                                    Sessao.usuarioAtual = null
                                    usuarioLogado = false
                                    navController.navigate("login") {
                                        popUpTo("principal") { inclusive = true }
                                    }
                                }
                            } ?: run {
                                navController.navigate("login")
                            }
                        }
                        composable("cadastroTarefa") {
                            Sessao.usuarioAtual?.let {
                                TelaCadastroTarefa(navController)
                            } ?: run {
                                navController.navigate("login")
                            }
                        }

                        composable("editarTarefa/{tarefaId}") { backStackEntry ->
                            val tarefaId = backStackEntry.arguments?.getString("tarefaId") ?: ""
                            Sessao.usuarioAtual?.let {
                                EditarTarefa(
                                    navController = navController,
                                    tarefaId = tarefaId,
                                    onSalvar = { tarefa ->
                                        // Aqui você pode implementar o salvamento da tarefa, por exemplo:
                                        println("Tarefa salva: ${tarefa.titulo}")
                                    }
                                )
                            } ?: run {
                                navController.navigate("login")
                            }
                        }

                        composable("removerTarefa/{tarefaId}") { backStackEntry ->
                            val tarefaId = backStackEntry.arguments?.getString("tarefaId") ?: ""
                            Sessao.usuarioAtual?.let {
                                RemoverTarefa(navController = navController, tarefaId = tarefaId)
                            } ?: run {
                                navController.navigate("login")
                            }
                        }
                        composable("listagem") {
                            Sessao.usuarioAtual?.let {
                                ListagemTarefa(navController)
                            } ?: run {
                                navController.navigate("login")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(usuarioLogado: Boolean, onLogoffClick: () -> Unit) {
    TopAppBar(
        title = { Text("App SmartCat", color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppBackground,
            titleContentColor = Color.White
        ),
        actions = {
            if (usuarioLogado) {
                IconButton(onClick = onLogoffClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sair",
                        tint = Color.White
                    )
                }
            }
        }
    )
}