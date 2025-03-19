package com.application.smartcat.ui.telas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.smartcat.R
import com.application.smartcat.model.Usuario
import com.application.smartcat.model.UsuarioDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaCadastro(modifier: Modifier = Modifier, onCadastroSucesso: () -> Unit, onCancelar: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Mantém os campos de login e senha
    var nome by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        // Imagem simulando uma logo (smartcat_logo2)
        Image(
            painter = painterResource(id = R.drawable.smartcat_logo2),
            contentDescription = "Logo Smartcat",
            modifier = Modifier.height(200.dp)
        )
        Spacer(modifier = Modifier.height(58.dp))
        // TextField do login
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text(text = "Nome") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // TextField da senha
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text(text = "Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Row contendo os Botões de "Cadastro" e "Cancelar"
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            // Botão de cadastro
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (nome.isNotEmpty() && senha.isNotEmpty()) {
                        // Cria um novo usuário e adiciona ao banco de dados
                        scope.launch(Dispatchers.IO) {
                            val usuario = Usuario(nome = nome, senha = senha)
                            UsuarioDAO().adicionar(usuario) { usuarioAdicionado ->
                                // Verifica se o usuário foi adicionado com sucesso
                                if (usuarioAdicionado != null) {
                                    onCadastroSucesso()
                                }
                                // Caso ocorra alguma falha durante o cadast
                                else {
                                    mensagemErro = "Falha ao criar sua conta. Tente novamente!"
                                }
                            }
                        }
                    }
                    // Mensagem de erro caso os campos estejam vazios
                    else {
                        mensagemErro = "Nome e senha são obrigatórios para continuar!"
                    }
                }
            ) {
                Text(
                    text = "Cadastrar",
                    fontSize = 18.sp
                )
            }
            // Botão de cancelar
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onCancelar() }
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 18.sp
                )
            }
        }
        mensagemErro?.let { erro ->
            LaunchedEffect(erro) {
                Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                mensagemErro = null
            }
        }
    }
}