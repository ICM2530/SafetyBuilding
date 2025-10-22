package com.example.main.CompReusable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.utils.theme.SafetyNeutral
import com.example.main.utils.theme.SafetyTextPrimary

@Composable
fun ReusableTopAppBar(
    title: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(Color.Transparent),
    textStyle: TextStyle = LocalTextStyle.current,
    leadingIcon: ImageVector? = Icons.Outlined.RemoveRedEye,
    leadingContentDescription: String? = null,
    onLeadingClick: (() -> Unit)? = null,
    trailingIcon: ImageVector? = Icons.Outlined.Person,
    trailingContentDescription: String? = null,
    onTrailingClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    showDivider: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leadingIcon?.let { icon ->
                        if (onLeadingClick != null) {
                            IconButton(onClick = onLeadingClick) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = leadingContentDescription,
                                    tint = SafetyTextPrimary
                                )
                            }
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = leadingContentDescription,
                                tint = SafetyTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text(
                        text = title,
                        style = textStyle.merge(
                            TextStyle(
                                color = SafetyTextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                }

                if (actions != null) {
                    actions.invoke(this)
                } else {
                    trailingIcon?.let { icon ->
                        if (onTrailingClick != null) {
                            IconButton(onClick = onTrailingClick) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = trailingContentDescription,
                                    tint = SafetyTextPrimary
                                )
                            }
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = trailingContentDescription,
                                tint = SafetyTextPrimary
                            )
                        }
                    }
                }
            }

            if (showDivider) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    thickness = 1.dp,
                    color = SafetyNeutral
                )
            }
        }
    }
}
