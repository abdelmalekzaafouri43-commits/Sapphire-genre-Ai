package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SapphirePrimary

/**
 * ApiKeyBoxCard Composable
 *
 * Provides a UI section for viewing and pasting a Gemini API Key,
 * with a "Test Key" button to test the key against the Google Gemini API in real-time.
 */
@Composable
fun ApiKeyBoxCard(
    apiKey: String,
    isTestingKey: Boolean,
    testResult: String?,
    isKeyValid: Boolean?,
    onApiKeyChange: (String) -> Unit,
    onTestKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        useGradientBorder = true,
        cornerRadius = 20.dp,
        padding = 18.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = SapphirePrimary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Key",
                            tint = SapphirePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Gemini API Key Settings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Enter or test your API key for live generation",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(text = if (isKeyValid == true) "KEY VALID" else "CONFIG")
            }

            // Input Text Box
            OutlinedTextField(
                value = apiKey,
                onValueChange = { onApiKeyChange(it) },
                label = { Text("Gemini API Key", fontSize = 12.sp) },
                placeholder = { Text("AI Studio Key or Custom Gemini Key...") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onTestKey(apiKey) }),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Key Icon",
                        tint = SapphirePrimary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide API Key" else "Show API Key",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SapphirePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_key_input_box")
            )

            // Test Key Button
            Button(
                onClick = { onTestKey(apiKey) },
                enabled = apiKey.isNotBlank() && !isTestingKey,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SapphirePrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("test_api_key_button")
            ) {
                if (isTestingKey) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Testing API Key with Gemini...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Test & Verify API Key",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Results Feedback Banner
            AnimatedVisibility(
                visible = testResult != null || isTestingKey,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val (bgColor, contentColor, icon) = when {
                    isTestingKey -> Triple(
                        SapphirePrimary.copy(alpha = 0.12f),
                        SapphirePrimary,
                        null
                    )
                    isKeyValid == true -> Triple(
                        Color(0xFFDCFCE7),
                        Color(0xFF166534),
                        Icons.Default.CheckCircle
                    )
                    else -> Triple(
                        Color(0xFFFEE2E2),
                        Color(0xFF991B1B),
                        Icons.Default.Error
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bgColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isTestingKey) {
                            CircularProgressIndicator(
                                color = contentColor,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "Test Result Icon",
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = testResult ?: "Verifying key connection...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
