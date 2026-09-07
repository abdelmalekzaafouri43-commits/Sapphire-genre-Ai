package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContentGenerator
import com.example.data.WorksheetRequest
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary
import kotlinx.coroutines.launch

/**
 * WorksheetGenerator Composable
 *
 * Includes input text fields for Topic and Grade Level for English Language worksheets,
 * and a button to trigger Gemini API generation of English Language worksheet content.
 */
@Composable
fun WorksheetGenerator(
    modifier: Modifier = Modifier,
    initialTopic: String = "English Grammar, Reading Comprehension & Vocabulary",
    initialGradeLevel: String = "Grade 8",
    onGenerateWorksheet: ((subject: String, gradeLevel: String, topic: String) -> Unit)? = null
) {
    var topic by remember { mutableStateOf(initialTopic) }
    var gradeLevel by remember { mutableStateOf(initialGradeLevel) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedWorksheetText by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        useGradientBorder = true,
        cornerRadius = 24.dp,
        padding = 20.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "English Language",
                            tint = SapphirePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "English Language Worksheet Generator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Powered by Gemini AI Engine",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(text = "ENGLISH AI", isPro = false)
            }

            // Grade Level Input Text Field
            OutlinedTextField(
                value = gradeLevel,
                onValueChange = { gradeLevel = it },
                label = { Text("Grade Level", fontSize = 12.sp) },
                placeholder = { Text("e.g. Grade 8, High School, Middle School") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Grade Level Icon",
                        tint = SapphirePrimary
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SapphirePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("grade_level_input")
            )

            // Topic Input Text Field
            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Topic / Prompt", fontSize = 12.sp) },
                placeholder = { Text("e.g. Tenses, Reading Comprehension, Synonyms & Antonyms...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Topic Icon",
                        tint = SapphirePrimary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SapphirePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("topic_input")
            )

            // Button to Trigger Gemini API Generation
            Button(
                onClick = {
                    if (topic.isNotBlank() && !isGenerating) {
                        isGenerating = true
                        onGenerateWorksheet?.invoke("English Language", gradeLevel, topic)

                        coroutineScope.launch {
                            val request = WorksheetRequest(
                                subject = "English Language",
                                gradeLevel = gradeLevel,
                                topic = topic,
                                questionType = "Mixed (Multiple Choice, Grammar & Vocabulary)",
                                questionCount = 8,
                                difficulty = "Medium",
                                includeAnswerKey = true
                            )
                            val doc = ContentGenerator.generateWorksheet(request)
                            generatedWorksheetText = doc.content
                            isGenerating = false
                        }
                    }
                },
                enabled = topic.isNotBlank() && !isGenerating,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SapphirePrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_worksheet_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Generating English Worksheet with Gemini...",
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
                        text = "Generate English Worksheet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Display Generated Content Output
            AnimatedVisibility(visible = generatedWorksheetText != null) {
                generatedWorksheetText?.let { content ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "GENERATED ENGLISH WORKSHEET",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SapphireSecondary
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("English Worksheet", content)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Worksheet copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Content",
                                        tint = SapphirePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .verticalScroll(rememberScrollState())
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = content,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
