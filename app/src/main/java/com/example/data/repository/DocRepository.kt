package com.example.data.repository

import com.example.data.local.DocDao
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import com.example.data.model.PresentationData
import com.example.data.model.WorksheetData
import com.example.data.remote.GeminiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocRepository(
    private val docDao: DocDao,
    private val geminiService: GeminiService
) {
    val allDocs: Flow<List<GeneratedDoc>> = docDao.getAllDocs()
    val docCount: Flow<Int> = docDao.getDocCount()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val worksheetAdapter = moshi.adapter(WorksheetData::class.java)
    private val presentationAdapter = moshi.adapter(PresentationData::class.java)

    suspend fun generateAndSaveWorksheet(
        subject: String,
        gradeLevel: String,
        topic: String,
        questionTypes: List<String>,
        questionCount: Int,
        difficulty: String
    ): Pair<GeneratedDoc, WorksheetData> {
        val data = geminiService.generateWorksheet(
            subject, gradeLevel, topic, questionTypes, questionCount, difficulty
        )

        val jsonString = worksheetAdapter.toJson(data)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val doc = GeneratedDoc(
            title = data.title,
            docType = DocType.WORKSHEET,
            subjectOrTopic = "$subject - $topic",
            gradeOrAudience = gradeLevel,
            dateCreated = dateStr,
            contentJson = jsonString,
            slideCountOrQuestionCount = data.questions.size,
            styleOrType = difficulty
        )

        val id = docDao.insertDoc(doc)
        val savedDoc = doc.copy(id = id)
        return Pair(savedDoc, data)
    }

    suspend fun generateAndSavePresentation(
        topic: String,
        targetAudience: String,
        slideCount: Int,
        designStyle: String,
        tone: String
    ): Pair<GeneratedDoc, PresentationData> {
        val data = geminiService.generatePresentation(
            topic, targetAudience, slideCount, designStyle, tone
        )

        val jsonString = presentationAdapter.toJson(data)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val doc = GeneratedDoc(
            title = data.title,
            docType = DocType.PRESENTATION,
            subjectOrTopic = topic,
            gradeOrAudience = targetAudience,
            dateCreated = dateStr,
            contentJson = jsonString,
            slideCountOrQuestionCount = data.slides.size,
            styleOrType = designStyle
        )

        val id = docDao.insertDoc(doc)
        val savedDoc = doc.copy(id = id)
        return Pair(savedDoc, data)
    }

    fun parseWorksheet(json: String): WorksheetData? {
        return try {
            worksheetAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun parsePresentation(json: String): PresentationData? {
        return try {
            presentationAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        docDao.toggleFavorite(id, isFavorite)
    }

    suspend fun deleteDoc(id: Long) {
        docDao.deleteDoc(id)
    }

    suspend fun getDocById(id: Long): GeneratedDoc? {
        return docDao.getDocById(id)
    }
}
