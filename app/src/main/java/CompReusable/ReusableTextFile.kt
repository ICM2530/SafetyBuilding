package com.example.main.CompReusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetyNeutralLight
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary

@Composable
fun ReusableTextField(
    contenido: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
    singleLine: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        readOnly = readOnly,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier.clip(RoundedCornerShape(22.dp)),
        placeholder = {
            Text(
                text = contenido,
                color = SafetyTextSecondary
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SafetyNeutralLight,
            unfocusedContainerColor = SafetyNeutralLight,
            disabledContainerColor = SafetyNeutralLight,
            cursorColor = SafetyGreenPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedPlaceholderColor = SafetyTextSecondary,
            unfocusedPlaceholderColor = SafetyTextSecondary,
            focusedTextColor = SafetyTextPrimary,
            unfocusedTextColor = SafetyTextPrimary
        ),
        shape = RoundedCornerShape(22.dp)
    )
}
