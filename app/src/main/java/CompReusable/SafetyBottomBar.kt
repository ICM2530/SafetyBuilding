package com.example.main.CompReusable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.utils.theme.SafetyGreenDark
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetyNeutral
import com.example.main.utils.theme.SafetyNeutralLight
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary

data class SafetyBottomBarItem(
    val icon: ImageVector,
    val label: String,
    val isSelected: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun SafetyBottomBar(
    items: List<SafetyBottomBarItem>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Surface(
        color = Color.Transparent,
        shadowElevation = 12.dp,
        tonalElevation = 0.dp,
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(SafetyGreenDark)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (item.isSelected) SafetyGreenPrimary else Color.Transparent
                        )
                        .clickable { item.onClick() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (item.isSelected) SafetyNeutralLight else SafetyNeutral
                    )
                    if (item.isSelected) {
                        Text(
                            text = item.label,
                            style = TextStyle(
                                color = SafetyNeutralLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
