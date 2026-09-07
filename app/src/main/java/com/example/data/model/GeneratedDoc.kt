package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DocType {
    WORKSHEET,
    PRESENTATION
}

@Entity(tableName = "generated_docs")
data class GeneratedDoc(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val docType: DocType,
    val subjectOrTopic: String,
    val gradeOrAudience: String,
    val dateCreated: String,
    val contentJson: String, // Formatted worksheet questions or slide deck JSON
    val slideCountOrQuestionCount: Int,
    val styleOrType: String,
    val isFavorite: Boolean = false
)

data class WorksheetQuestion(
    val id: Int,
    val questionText: String,
    val questionType: String, // "Multiple Choice", "Short Answer", "True/False", "Fill in Blank"
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val explanation: String = ""
)

data class WorksheetData(
    val title: String,
    val subject: String,
    val gradeLevel: String,
    val topic: String,
    val instructions: String,
    val questions: List<WorksheetQuestion>
)

data class SlideItem(
    val slideNumber: Int,
    val title: String,
    val subtitle: String = "",
    val bulletPoints: List<String>,
    val visualSuggestion: String = "",
    val speakerNotes: String = ""
)

data class PresentationData(
    val title: String,
    val topic: String,
    val targetAudience: String,
    val designStyle: String,
    val slides: List<SlideItem>
)
