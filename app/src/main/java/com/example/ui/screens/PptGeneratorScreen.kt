package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import com.example.data.model.PresentationData
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PptGeneratorScreen(
    isGenerating: Boolean,
    lastGeneratedDoc: GeneratedDoc?,
    lastPresentationData: PresentationData?,
    onGeneratePresentation: (
        topic: String,
        targetAudience: String,
        slideCount: Int,
        designStyle: String,
        tone: String
    ) -> Unit,
    onOpenPreview: (GeneratedDoc) -> Unit,
    modifier: Modifier = Modifier
) {
    val audiences = listOf("Students / Learners", "Executive Leadership", "Investors & Pitching", "General Audience", "Academic Conference")
    val designStyles = listOf("Minimal Sapphire", "Executive Slate", "Creative Electric", "Dark Midnight", "Academic Clean")
    val tones = listOf("Educational", "Informative", "Persuasive", "Energetic", "Formal")

    var topicInput by remember { mutableStateOf("Modern AI Architecture & Neural Networks") }
    var selectedAudience by remember { mutableStateOf(audiences[0]) }
    var selectedStyle by remember { mutableStateOf(designStyles[0]) }
    var selectedTone by remember { mutableStateOf(tones[0]) }
    var slideCount by remember { mutableFloatStateOf(8f) }

    var audienceExpanded by remember { mutableStateOf(false) }
    var styleExpanded by remember { mutableStateOf(false) }
    var toneExpanded by remember { mutableStateOf(false) }

    val samplePptTopics = listOf(
        "Modern AI Architecture & Neural Networks",
        "Global Climate Action & Renewable Energy",
        "SaaS Startup Pitch Deck 2026",
        "Cybersecurity Fundamentals & Protocols",
        "Cell Biology & Molecular Genetics"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            useGradientBorder = true,
            cornerRadius = 24.dp,
            padding = 20.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PowerPoint Generator",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Generate slide deck outlines with visual suggestions & notes",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(text = "Pro Feature", isPro = true)
                }

                // Topic Input
                OutlinedTextField(
                    value = topicInput,
                    onValueChange = { topicInput = it },
                    label = { Text("Presentation Topic / Title", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Modern Architecture, AI Trends...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SapphirePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Topic Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "POPULAR PPT TOPICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SapphirePrimary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        samplePptTopics.forEach { sample ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (topicInput == sample) SapphirePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { topicInput = sample }
                            ) {
                                Text(
                                    text = sample,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (topicInput == sample) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // Audience & Design Style
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = audienceExpanded,
                        onExpandedChange = { audienceExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedAudience,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Audience", fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SapphirePrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = audienceExpanded,
                            onDismissRequest = { audienceExpanded = false }
                        ) {
                            audiences.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedAudience = item
                                        audienceExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = styleExpanded,
                        onExpandedChange = { styleExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedStyle,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Design Style", fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SapphirePrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = styleExpanded,
                            onDismissRequest = { styleExpanded = false }
                        ) {
                            designStyles.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedStyle = item
                                        styleExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Slide Count & Tone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Slide Count:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${slideCount.toInt()} Slides", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SapphirePrimary)
                        }
                        Slider(
                            value = slideCount,
                            onValueChange = { slideCount = it },
                            valueRange = 5f..15f,
                            steps = 1,
                            colors = SliderDefaults.colors(
                                thumbColor = SapphireSecondary,
                                activeTrackColor = SapphireSecondary
                            )
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = toneExpanded,
                        onExpandedChange = { toneExpanded = it },
                        modifier = Modifier.width(130.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedTone,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tone", fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SapphirePrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = toneExpanded,
                            onDismissRequest = { toneExpanded = false }
                        ) {
                            tones.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedTone = item
                                        toneExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Action Button
                Button(
                    onClick = {
                        if (!isGenerating && topicInput.isNotBlank()) {
                            onGeneratePresentation(
                                topicInput,
                                selectedAudience,
                                slideCount.toInt(),
                                selectedStyle,
                                selectedTone
                            )
                        }
                    },
                    enabled = !isGenerating && topicInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SapphireSecondary
                    )
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Drafting PPT Slides...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.Slideshow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate PPT Slides", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Generated Output Card Preview
        lastGeneratedDoc?.let { doc ->
            if (doc.docType == DocType.PRESENTATION && lastPresentationData != null) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    useGradientBorder = true,
                    cornerRadius = 24.dp,
                    padding = 20.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = "SLIDE DECK READY", isPro = true)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = doc.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Button(
                                onClick = { onOpenPreview(doc) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SapphireSecondary)
                            ) {
                                Icon(imageVector = Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Interactive Deck", fontSize = 11.sp)
                            }
                        }

                        Text(
                            text = "Audience: ${lastPresentationData.targetAudience} • Style: ${lastPresentationData.designStyle}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Slide Structure (${lastPresentationData.slides.size} Slides):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SapphireSecondary
                        )

                        lastPresentationData.slides.take(3).forEach { slide ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "[Slide ${slide.slideNumber}]",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = SapphireSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = slide.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
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
