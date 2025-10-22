package com.example.main.CompReusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetyNeutral
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White

@Composable
fun ReusableButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SafetyGreenPrimary,
            contentColor = White,
            disabledContainerColor = SafetyNeutral,
            disabledContentColor = SafetyTextSecondary
        ),
    ) {
        Text(
            label,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

        )
    }
}
