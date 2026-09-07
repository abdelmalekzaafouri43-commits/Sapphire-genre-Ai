package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import com.example.data.model.PresentationData
import com.example.data.model.WorksheetData
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary

@Composable
fun DocPreviewDialog(
    doc: GeneratedDoc,
    worksheetData: WorksheetData?,
    presentationData: PresentationData?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showAnswerKey by remember { mutableStateOf(false) }
    var currentSlideIndex by remember { mutableStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(
                            text = if (doc.docType == DocType.WORKSHEET) "WORKSHEET" else "PRESENTATION",
                            isPro = false
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = doc.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar (Copy, Export, Answer Key Toggle)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (doc.docType == DocType.WORKSHEET) {
                        OutlinedButton(
                            onClick = { showAnswerKey = !showAnswerKey },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (showAnswerKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showAnswerKey) "Hide Answer Key" else "Show Answer Key", fontSize = 12.sp)
                        }
                    } else if (presentationData != null && presentationData.slides.isNotEmpty()) {
                        Text(
                            text = "Slide ${currentSlideIndex + 1} of ${presentationData.slides.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SapphirePrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipText = buildExportString(doc, worksheetData, presentationData, showAnswerKey)
                                val clip = ClipData.newPlainText("Generated Content", clipText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Content copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Content", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // Content View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (doc.docType == DocType.WORKSHEET && worksheetData != null) {
                        WorksheetView(data = worksheetData, showAnswerKey = showAnswerKey)
                    } else if (doc.docType == DocType.PRESENTATION && presentationData != null) {
                        PresentationView(
                            data = presentationData,
                            currentIndex = currentSlideIndex,
                            onIndexChange = { currentSlideIndex = it }
                        )
                    } else {
                        Text(text = "No content available.", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
private fun WorksheetView(data: WorksheetData, showAnswerKey: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Formatted Document Sheet
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Name: ____________________", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Date: ___________", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subject: ${data.subject}", fontSize = 12.sp, color = SapphirePrimary, fontWeight = FontWeight.Bold)
                    Text("Grade: ${data.gradeLevel}", fontSize = 12.sp, color = SapphirePrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Instructions: ${data.instructions}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Questions List
        data.questions.forEachIndexed { index, q ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                padding = 16.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Q${index + 1}. ${q.questionText}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (q.options.isNotEmpty()) {
                        q.options.forEach { opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SapphirePrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = opt, fontSize = 13.sp)
                            }
                        }
                    } else {
                        // Lines for short answer
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        }
                    }

                    // Answer Key Expandable
                    AnimatedVisibility(visible = showAnswerKey) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "✔ Correct Answer: ${q.correctAnswer}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                                if (q.explanation.isNotBlank()) {
                                    Text(
                                        text = "Explanation: ${q.explanation}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF166534)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresentationView(
    data: PresentationData,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit
) {
    if (data.slides.isEmpty()) return

    val slide = data.slides[currentIndex.coerceIn(0, data.slides.size - 1)]

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Slide Canvas Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxSize(),
                useGradientBorder = true,
                cornerRadius = 20.dp,
                padding = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SLIDE ${slide.slideNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SapphirePrimary
                        )
                        StatusBadge(text = data.designStyle, isPro = true)
                    }

                    Text(
                        text = slide.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (slide.subtitle.isNotBlank()) {
                        Text(
                            text = slide.subtitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = SapphireSecondary
                        )
                    }

                    Divider()

                    // Bullet points
                    slide.bulletPoints.forEach { point ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text("✦ ", fontSize = 14.sp, color = SapphirePrimary, fontWeight = FontWeight.Bold)
                            Text(text = point, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    if (slide.visualSuggestion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("💡 Visual Layout Suggestion:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SapphirePrimary)
                                Text(slide.visualSuggestion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    if (slide.speakerNotes.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🎙 Speaker Notes:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                Text(slide.speakerNotes, fontSize = 12.sp, color = Color(0xFF92400E))
                            }
                        }
                    }
                }
            }
        }

        // Slide Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentIndex > 0) onIndexChange(currentIndex - 1) },
                enabled = currentIndex > 0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
            ) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous")
                Text("Previous")
            }

            Text(
                text = "${currentIndex + 1} / ${data.slides.size}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Button(
                onClick = { if (currentIndex < data.slides.size - 1) onIndexChange(currentIndex + 1) },
                enabled = currentIndex < data.slides.size - 1,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
            ) {
                Text("Next")
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next")
            }
        }
    }
}

private fun buildExportString(
    doc: GeneratedDoc,
    worksheetData: WorksheetData?,
    presentationData: PresentationData?,
    includeAnswers: Boolean
): String {
    val sb = StringBuilder()
    sb.appendLine("=== ${doc.title} ===")
    sb.appendLine("Created via Sapphire AI Content Generator")
    sb.appendLine()

    if (doc.docType == DocType.WORKSHEET && worksheetData != null) {
        sb.appendLine("Subject: ${worksheetData.subject} | Grade: ${worksheetData.gradeLevel}")
        sb.appendLine("Instructions: ${worksheetData.instructions}")
        sb.appendLine()
        worksheetData.questions.forEachIndexed { i, q ->
            sb.appendLine("Q${i + 1}. ${q.questionText}")
            q.options.forEach { opt -> sb.appendLine("   - $opt") }
            if (includeAnswers) {
                sb.appendLine("   [Answer: ${q.correctAnswer}]")
            }
            sb.appendLine()
        }
    } else if (doc.docType == DocType.PRESENTATION && presentationData != null) {
        sb.appendLine("Topic: ${presentationData.topic} | Audience: ${presentationData.targetAudience}")
        sb.appendLine("Style: ${presentationData.designStyle}")
        sb.appendLine()
        presentationData.slides.forEach { slide ->
            sb.appendLine("--- SLIDE ${slide.slideNumber}: ${slide.title} ---")
            if (slide.subtitle.isNotBlank()) sb.appendLine("Subtitle: ${slide.subtitle}")
            slide.bulletPoints.forEach { point -> sb.appendLine(" • $point") }
            if (slide.speakerNotes.isNotBlank()) sb.appendLine("Speaker Notes: ${slide.speakerNotes}")
            sb.appendLine()
        }
    }

    return sb.toString()
}
