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
import androidx.compose.material3.TextButton
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
import com.application.smartcat.model.UsuarioDAO
import com.application.smartcat.util.Sessao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaLogin(modifier: Modifier = Modifier, onLogarClick: () -> Unit, onCadastroClick: () -> Unit) {
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
        // Imagem simulando uma logo (smartcat_logo)
        Image(
            painter = painterResource(id = R.drawable.smartcat_logo),
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
        // Espaçamento entre o TextField do login e o TextField da senha
        Spacer(modifier = Modifier.height(16.dp))
        // TextField da senha
        OutlinedTextField(
            value = senha,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { senha = it },
            label = { Text(text = "Senha") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Row contendo o botão de "Login"
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            // Botão de login
            Button(modifier = Modifier.weight(1f), onClick = {
                scope.launch(Dispatchers.IO) {
                    // Verifica se o usuário existe e se a senha está correta
                    UsuarioDAO().buscarPorNome(nome) { usuario ->
                        if (usuario != null && usuario.senha == senha) {
                            Sessao.usuarioAtual = usuario
                            onLogarClick()
                        }
                        else {
                            mensagemErro = "Nome ou senha inválidos!"
                        }
                    }
                }
            }) {
                Text(
                    text = "Entrar",
                    fontSize = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // TextButton para criar uma conta
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Não possui uma conta?")
            TextButton(onClick = { onCadastroClick() }) {
                Text(
                    text = "Crie uma conta",
                    fontSize = 18.sp
                )
            }
        }
        mensagemErro?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                mensagemErro = null
            }
        }
    }
}