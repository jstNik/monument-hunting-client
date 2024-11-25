package com.example.monument_hunting.ui.composables

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monument_hunting.activities.LoginActivity
import com.example.monument_hunting.activities.MapsActivity
import com.example.monument_hunting.domain.api_domain._Player
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.view_models.LoginSignupViewModel
import com.google.gson.Gson

private enum class TextFields{
    Username{
        override var textFieldValue by mutableStateOf(TextFieldValue())
        override val label = "Username"
        override val hint = "Enter a username"
        override val keyboardType = KeyboardType.Text
        override val imeAction = ImeAction.Next
        override val platformImeOptions = PlatformImeOptions("Next")
        override val visualTransformation = VisualTransformation.None
    },
    Email{
        override var textFieldValue by mutableStateOf(TextFieldValue())
        override val label: String = "Email"
        override val hint: String = "Enter an email"
        override val keyboardType = KeyboardType.Email
        override val imeAction = ImeAction.Next
        override val platformImeOptions = PlatformImeOptions("Next")
        override val visualTransformation = VisualTransformation.None
    },
    Password{
        override var textFieldValue by mutableStateOf(TextFieldValue())
        override val label = "Password"
        override val hint = "Enter a password"
        override val keyboardType = KeyboardType.Password
        override val imeAction = ImeAction.Send
        override val platformImeOptions = PlatformImeOptions("Send")
        override val visualTransformation = PasswordVisualTransformation()
    };

    abstract var textFieldValue: TextFieldValue
    abstract val label: String
    abstract val hint: String
    abstract val keyboardType: KeyboardType
    abstract val imeAction: ImeAction
    abstract val platformImeOptions: PlatformImeOptions
    abstract val visualTransformation: VisualTransformation
}


@Composable
fun ComposeLoginView(){

    var signup by remember{
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier= Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
    ) {
        val annotatedText = buildAnnotatedString {
            append("Hello, welcome!\n")
            if(signup) {
                pushStringAnnotation(tag = "login", annotation = "login")
                pushStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
                append("Login")
                pop()
                pop()
                pushStringAnnotation(tag = "signup", annotation = "signup")
                append(" or signup")
                pop()
            }else{
                pushStringAnnotation(tag = "login", annotation = "login")
                append("Login")
                pop()
                append(" or ")
                pushStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
                pushStringAnnotation(tag = "signup", annotation = "signup")
                append("signup")
                pop()
                pop()
            }
            append(" to continue")

        }

        var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

        Text(
            text = annotatedText,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { pos ->
                    layoutResult?.let { layoutResult ->
                        val offset = layoutResult.getOffsetForPosition(pos)
                        annotatedText.getStringAnnotations(
                            if (!signup) "signup" else "login",
                            offset,
                            offset
                        ).firstOrNull()?.let {
                            signup = !signup
                        }
                    }
                }
            },
            onTextLayout = {
                layoutResult = it
            }
        )

        LoginSignupForm(
            signup=signup,
            onSuccess = { player ->
                player?.let{
                    Text(
                        "✔ Success",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    val intent = Intent(context, MapsActivity::class.java)
                    intent.putExtra("player", Gson().toJson(it))
                    context.startActivity(intent)
                    (context as? LoginActivity)?.finish()
                }
            },
            onLoading = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 1.dp,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(16.dp)
                    )
                    Text(
                        "Loading",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier=Modifier.padding()
                    )
                }
            },
            onFailure = { }
        )

    }

}

@Composable
fun LoginSignupForm(
    signup: Boolean,
    onSuccess: @Composable (valid: _Player?) -> Unit,
    onLoading: @Composable () -> Unit,
    onFailure: @Composable (error: String?) -> Unit
){

    val focusManager = LocalFocusManager.current
    val loginSignupViewModel = viewModel<LoginSignupViewModel>()
    val result by loginSignupViewModel.loginSignupSuc.collectAsStateWithLifecycle()
    val allFieldsWrong = result.status == Data.Status.Error &&
            result.error?.contains(Regex("username|password|email", RegexOption.IGNORE_CASE)) == false

    TextFields.entries.forEachIndexed { i, entry ->
        if (!signup && entry == TextFields.Email)
            return@forEachIndexed
        val focusRequester = remember{ FocusRequester() }
        var error = false
        if (result.status == Data.Status.Error && !allFieldsWrong) {
            error = result.error?.contains("username", true) == true && entry == TextFields.Username
                    || result.error?.contains("password", true) == true && entry == TextFields.Password
                    || result.error?.contains("email", true) == true && entry == TextFields.Email
        }

        TextField(
            value = entry.textFieldValue,
            onValueChange = {
                entry.textFieldValue = it
            },
            isError = error || allFieldsWrong,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = entry.keyboardType,
                imeAction = entry.imeAction,
                platformImeOptions = entry.platformImeOptions,
                showKeyboardOnFocus = true
            ),
            keyboardActions = if (entry.imeAction == ImeAction.Send) KeyboardActions {
                loginSignupViewModel.loginSignup(
                    signup,
                    TextFields.Username.textFieldValue.text,
                    TextFields.Email.textFieldValue.text,
                    TextFields.Password.textFieldValue.text
                )
            } else KeyboardActions.Default,
            visualTransformation = entry.visualTransformation,
            label = { Text(if (error && result.error != null) result.error!! else entry.label) },
            placeholder = { Text(entry.hint) },
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .focusRequester(focusRequester)
        )
        if(i == TextFields.entries.size - 1 && allFieldsWrong)
            Box {
                Text(
                    result.error ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
    }

    when(result.status){
        Data.Status.Success -> {
            onSuccess(result.data)
        }
        Data.Status.Loading -> {
            onLoading()
        }
        Data.Status.Error -> {
            onFailure(result.error)
        }
    }

    TextButton(
        onClick={
            focusManager.clearFocus()
            loginSignupViewModel.loginSignup(
                signup,
                TextFields.Username.textFieldValue.text,
                TextFields.Email.textFieldValue.text,
                TextFields.Password.textFieldValue.text
            )
        },
        colors = ButtonDefaults.buttonColors(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(if(signup) "Sign up" else "Log in")
    }

}

@Preview
@Composable
private fun PrevLoginSignupForm(){
    MonumentHuntingTheme {
        ComposeLoginView()
    }
}