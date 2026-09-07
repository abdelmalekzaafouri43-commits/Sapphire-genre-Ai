package com.example.data

enum class DocumentType {
    WORKSHEET,
    PRESENTATION
}

data class GeneratedDocument(
    val id: String,
    val type: DocumentType,
    val title: String,
    val subjectOrTopic: String,
    val gradeOrAudience: String,
    val itemCount: Int, // Number of questions or slides
    val createdAt: String,
    val content: String,
    val isFavorite: Boolean = false,
    val statusBadge: String = "AI Ready"
)

data class WorksheetRequest(
    val subject: String = "Mathematics",
    val gradeLevel: String = "Grade 8",
    val topic: String = "Algebraic Expressions & Linear Equations",
    val questionType: String = "Mixed (Multiple Choice & Word Problems)",
    val questionCount: Int = 10,
    val difficulty: String = "Medium",
    val includeAnswerKey: Boolean = true
)

data class PptRequest(
    val topic: String = "Modern Sustainable Architecture & Smart Cities",
    val targetAudience: String = "University Students & Tech Executives",
    val slideCount: Int = 12,
    val designStyle: String = "Minimal & Modern Glass",
    val includeSpeakerNotes: Boolean = true
)

data class StorageInfo(
    val usedMb: Double = 4250.0,
    val totalMb: Double = 5000.0,
    val generatedCount: Int = 142
) {
    val percentageUsed: Int
        get() = ((usedMb / totalMb) * 100).toInt()

    val formattedUsedGb: String
        get() = String.format("%.2f GB", usedMb / 1024.0)

    val formattedTotalGb: String
        get() = String.format("%.2f GB", totalMb / 1024.0)
}
