package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedDoc
import com.example.data.model.WorksheetData
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.WorksheetGenerator
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorksheetGeneratorScreen(
    isGenerating: Boolean,
    lastGeneratedDoc: GeneratedDoc?,
    lastWorksheetData: WorksheetData?,
    onGenerateWorksheet: (
        subject: String,
        gradeLevel: String,
        topic: String,
        questionTypes: List<String>,
        questionCount: Int,
        difficulty: String
    ) -> Unit,
    onOpenPreview: (GeneratedDoc) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects = listOf("English Language", "Mathematics", "Physics", "Chemistry", "Biology", "English Literature", "World History", "Computer Science", "Geography")
    val gradeLevels = listOf("Grade 5", "Grade 8", "Grade 10", "High School (AP)", "College / University")
    val difficulties = listOf("Easy", "Medium", "Hard", "Advanced")
    val availableQuestionTypes = listOf("Multiple Choice", "Short Answer", "True/False", "Fill in Blank")

    var selectedSubject by remember { mutableStateOf(subjects[0]) }
    var selectedGrade by remember { mutableStateOf(gradeLevels[1]) }
    var topicInput by remember { mutableStateOf("English Grammar & Vocabulary") }
    var selectedDifficulty by remember { mutableStateOf(difficulties[1]) }
    var questionCount by remember { mutableFloatStateOf(10f) }

    val selectedQuestionTypes = remember { mutableStateListOf("Multiple Choice", "Short Answer") }

    var subjectExpanded by remember { mutableStateOf(false) }
    var gradeExpanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }

    val sampleTopics = mapOf(
        "English Language" to listOf("English Grammar & Vocabulary", "Reading Comprehension & Passages", "Parts of Speech & Tenses", "Idioms, Synonyms & Antonyms"),
        "Mathematics" to listOf("Linear Equations & Graphing", "Pythagorean Theorem", "Quadratic Functions", "Probability & Statistics"),
        "Physics" to listOf("Newton's Laws of Motion", "Work & Energy", "Thermodynamics", "Wave Mechanics"),
        "Biology" to listOf("Photosynthesis & Respiration", "DNA Structure & Genetics", "Cellular Biology", "Ecosystem Dynamics"),
        "World History" to listOf("World War II Timeline", "Ancient Egyptian Civilization", "Industrial Revolution", "Cold War Era")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dedicated English Language Worksheet Generator
        WorksheetGenerator(
            initialTopic = "English Grammar, Reading Comprehension & Vocabulary",
            initialGradeLevel = "Grade 8",
            onGenerateWorksheet = { subject, grade, topic ->
                onGenerateWorksheet(subject, grade, topic, listOf("Multiple Choice", "Short Answer"), 8, "Medium")
            }
        )
        // Form Container with Glowing Gradient Border
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            useGradientBorder = true,
            cornerRadius = 24.dp,
            padding = 20.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Form Title & Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Worksheet Generator",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Configure parameters for instant worksheet generation",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(text = "AI Ready", isPro = false)
                }

                // Grid 1: Subject & Grade Level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Subject Dropdown
                    ExposedDropdownMenuBox(
                        expanded = subjectExpanded,
                        onExpandedChange = { subjectExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedSubject,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Subject", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
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
                            expanded = subjectExpanded,
                            onDismissRequest = { subjectExpanded = false }
                        ) {
                            subjects.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedSubject = item
                                        sampleTopics[item]?.firstOrNull()?.let { topicInput = it }
                                        subjectExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Grade Dropdown
                    ExposedDropdownMenuBox(
                        expanded = gradeExpanded,
                        onExpandedChange = { gradeExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedGrade,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Grade Level", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
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
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            gradeLevels.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedGrade = item
                                        gradeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Topic Input
                OutlinedTextField(
                    value = topicInput,
                    onValueChange = { topicInput = it },
                    label = { Text("Topic / Prompt", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Quadratic Equations, Photosynthesis...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SapphirePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Topic Suggestion Chips
                sampleTopics[selectedSubject]?.let { topics ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "SUGGESTED TOPICS FOR $selectedSubject",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SapphirePrimary
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            topics.forEach { suggestion ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (topicInput == suggestion) SapphirePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { topicInput = suggestion }
                                ) {
                                    Text(
                                        text = suggestion,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (topicInput == suggestion) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Question Types Checklist Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "QUESTION TYPES TO INCLUDE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableQuestionTypes.forEach { qType ->
                            val isChecked = selectedQuestionTypes.contains(qType)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isChecked) SapphirePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isChecked) androidx.compose.foundation.BorderStroke(1.dp, SapphirePrimary) else null,
                                modifier = Modifier.clickable {
                                    if (isChecked) {
                                        if (selectedQuestionTypes.size > 1) selectedQuestionTypes.remove(qType)
                                    } else {
                                        selectedQuestionTypes.add(qType)
                                    }
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = SapphirePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = qType,
                                        fontSize = 12.sp,
                                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isChecked) SapphirePrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Question Count Slider & Difficulty
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
                            Text("Question Count:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${questionCount.toInt()} Questions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SapphirePrimary)
                        }
                        Slider(
                            value = questionCount,
                            onValueChange = { questionCount = it },
                            valueRange = 5f..20f,
                            steps = 2,
                            colors = SliderDefaults.colors(
                                thumbColor = SapphirePrimary,
                                activeTrackColor = SapphirePrimary
                            )
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = difficultyExpanded,
                        onExpandedChange = { difficultyExpanded = it },
                        modifier = Modifier.width(130.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedDifficulty,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Difficulty", fontSize = 10.sp) },
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
                            expanded = difficultyExpanded,
                            onDismissRequest = { difficultyExpanded = false }
                        ) {
                            difficulties.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedDifficulty = item
                                        difficultyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Generate Button
                Button(
                    onClick = {
                        if (!isGenerating && topicInput.isNotBlank()) {
                            onGenerateWorksheet(
                                selectedSubject,
                                selectedGrade,
                                topicInput,
                                selectedQuestionTypes.toList(),
                                questionCount.toInt(),
                                selectedDifficulty
                            )
                        }
                    },
                    enabled = !isGenerating && topicInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SapphirePrimary
                    )
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating Worksheet...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Worksheet", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Generated Output Card Preview
        lastGeneratedDoc?.let { doc ->
            if (doc.docType == com.example.data.model.DocType.WORKSHEET && lastWorksheetData != null) {
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
                                StatusBadge(text = "GENERATED", isPro = false)
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
                                colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
                            ) {
                                Icon(imageVector = Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Preview & Export", fontSize = 11.sp)
                            }
                        }

                        Text(
                            text = "Instructions: ${lastWorksheetData.instructions}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Questions Preview (${lastWorksheetData.questions.size} Total):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SapphirePrimary
                        )

                        lastWorksheetData.questions.take(3).forEachIndexed { idx, q ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Q${idx + 1}. ${q.questionText}",
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
