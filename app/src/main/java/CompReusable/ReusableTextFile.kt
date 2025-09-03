package com.example.main.CompReusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReusableTextField(contenido: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(contenido) },
        value = value,
        onValueChange = onValueChange
    )
}