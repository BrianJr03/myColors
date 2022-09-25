package jr.brian.myapplication.view.ui.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jr.brian.myapplication.util.theme.BlueishIDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignUpPage(context: Context, launchHome: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cPassword by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Enter email") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Enter password") },
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Confirm Password
        OutlinedTextField(
            value = cPassword,
            onValueChange = { cPassword = it },
            label = { Text(text = "Confirm password") },
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedButton(
            onClick = {
                signUpAcct(
                    context = context,
                    scope = scope,
                    email = email,
                    password = password,
                    cPassword = cPassword,
                    launchHome = launchHome
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Text(text = "Sign Up", textAlign = TextAlign.Center, color =  BlueishIDK)
        }
    }
}

fun signUpAcct(
    scope: CoroutineScope,
    context: Context,
    email: String,
    password: String,
    cPassword: String,
    launchHome: () -> Unit
) {
    if (email.isEmpty() && password.isEmpty()) {
        Toast.makeText(context, "Please provide all required values", Toast.LENGTH_SHORT).show()
    } else {
        if (password == cPassword) {
            scope.launch {
                // TODO - SIGN UP USER WITH FIREBASE
                launchHome()
            }
            Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Passwords must match", Toast.LENGTH_SHORT).show()
        }
    }
}

