package com.example.main.CompReusable

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.utils.theme.Blue

@Composable
fun ReusableButton(label : String, onClick :() -> Unit, modifier : Modifier = Modifier.height(35.dp).width(122.dp)){
    Button(

        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Blue,
        ),
    ) {
        Text(
            label,
            style = TextStyle(
                fontSize = 16.sp
            )

        )
    }
}