package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

object ContentGenerator {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateWorksheet(request: WorksheetRequest): GeneratedDocument = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val generatedContent = if (!apiKey.isNullOrEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                callGeminiForWorksheet(apiKey, request)
            } catch (e: Exception) {
                buildFallbackWorksheet(request)
            }
        } else {
            buildFallbackWorksheet(request)
        }

        GeneratedDocument(
            id = UUID.randomUUID().toString().take(8),
            type = DocumentType.WORKSHEET,
            title = "${request.subject}: ${request.topic}",
            subjectOrTopic = request.subject,
            gradeOrAudience = request.gradeLevel,
            itemCount = request.questionCount,
            createdAt = getCurrentDateFormatted(),
            content = generatedContent,
            statusBadge = "AI Ready"
        )
    }

    suspend fun generatePpt(request: PptRequest): GeneratedDocument = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val generatedContent = if (!apiKey.isNullOrEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                callGeminiForPpt(apiKey, request)
            } catch (e: Exception) {
                buildFallbackPpt(request)
            }
        } else {
            buildFallbackPpt(request)
        }

        GeneratedDocument(
            id = UUID.randomUUID().toString().take(8),
            type = DocumentType.PRESENTATION,
            title = request.topic,
            subjectOrTopic = request.topic,
            gradeOrAudience = request.targetAudience,
            itemCount = request.slideCount,
            createdAt = getCurrentDateFormatted(),
            content = generatedContent,
            statusBadge = "Pro Feature"
        )
    }

    private fun callGeminiForWorksheet(apiKey: String, request: WorksheetRequest): String {
        val prompt = """
            Create a professional academic worksheet for subject "${request.subject}", grade level "${request.gradeLevel}", topic "${request.topic}".
            Format: Include a Header section, ${request.questionCount} numbered questions (${request.questionType}, difficulty ${request.difficulty}), and an Answer Key with explanations at the bottom.
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
        }

        val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val body = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder().url(requestUrl).post(body).build()

        val response = client.newCall(httpRequest).execute()
        val responseBody = response.body?.string() ?: ""

        return parseGeminiResponse(responseBody) ?: buildFallbackWorksheet(request)
    }

    private fun callGeminiForPpt(apiKey: String, request: PptRequest): String {
        val prompt = """
            Create a PowerPoint presentation slide outline on topic "${request.topic}" for target audience "${request.targetAudience}".
            Style: ${request.designStyle}. Number of slides: ${request.slideCount}.
            For each slide, include:
            - Slide Title
            - Key Bullet Points (3-4 points)
            - Speaker Notes
            - Visual Layout & Color Palette Recommendation
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
        }

        val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val body = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder().url(requestUrl).post(body).build()

        val response = client.newCall(httpRequest).execute()
        val responseBody = response.body?.string() ?: ""

        return parseGeminiResponse(responseBody) ?: buildFallbackPpt(request)
    }

    private fun parseGeminiResponse(jsonString: String): String? {
        return try {
            val json = JSONObject(jsonString)
            val candidates = json.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val parts = candidate.getJSONObject("content").getJSONArray("parts")
                if (parts.length() > 0) {
                    parts.getJSONObject(0).getString("text")
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun buildFallbackWorksheet(request: WorksheetRequest): String {
        val sb = StringBuilder()
        sb.append("===================================================\n")
        sb.append("         ${request.subject.uppercase()} WORKSHEET\n")
        sb.append("Grade Level: ${request.gradeLevel} | Difficulty: ${request.difficulty}\n")
        sb.append("Topic: ${request.topic}\n")
        sb.append("===================================================\n\n")

        sb.append("Student Name: _______________________ Date: _________\n")
        sb.append("Score: ____ / ${request.questionCount}\n\n")

        sb.append("PART I: PRACTICE EXERCISES (${request.questionType.uppercase()})\n\n")

        for (i in 1..request.questionCount) {
            when (i % 4) {
                1 -> {
                    sb.append("$i. Evaluate the primary equation for ${request.topic}:\n")
                    sb.append("    a) 2x + 5 = 15  (Find x)\n")
                    sb.append("    b) Option A: x = 5\n")
                    sb.append("    c) Option B: x = 10\n")
                    sb.append("    d) Option C: x = 2.5\n\n")
                }
                2 -> {
                    sb.append("$i. True or False: In ${request.topic}, the rate of change remains constant across isotropic systems.\n")
                    sb.append("    [  ] True      [  ] False\n\n")
                }
                3 -> {
                    sb.append("$i. Fill in the blank: The fundamental principle governing ${request.topic} is known as the ________________ theorem.\n\n")
                }
                0 -> {
                    sb.append("$i. Real-World Application Problem:\n")
                    sb.append("    A researcher observes a 25% variance in ${request.topic} outcomes during testing. Calculate the expected standard coefficient.\n")
                    sb.append("    Workspace / Working Out:\n\n\n\n")
                }
            }
        }

        if (request.includeAnswerKey) {
            sb.append("\n---------------------------------------------------\n")
            sb.append("               ANSWER KEY & EXPLANATIONS\n")
            sb.append("---------------------------------------------------\n")
            for (i in 1..request.questionCount) {
                val ans = when (i % 4) {
                    1 -> "1. Option A (x = 5). Subtract 5 from 15 to get 10, then divide by 2."
                    2 -> "2. True. Isotropic systems maintain uniform directional properties."
                    3 -> "3. Fundamental / Equilibrium Theorem."
                    else -> "4. Expected coefficient = 0.80. Derived from standard normalized variance."
                }
                sb.append("$ans\n")
            }
        }

        return sb.toString()
    }

    fun buildFallbackPpt(request: PptRequest): String {
        val sb = StringBuilder()
        sb.append("===================================================\n")
        sb.append("  PRESENTATION DECK: ${request.topic.uppercase()}\n")
        sb.append("Audience: ${request.targetAudience} | Design Style: ${request.designStyle}\n")
        sb.append("===================================================\n\n")

        for (i in 1..request.slideCount) {
            val title = when (i) {
                1 -> "Title: Executive Summary & Overview of ${request.topic}"
                2 -> "Title: Background, Problem Statement & Market Context"
                3 -> "Title: Core Architecture & Strategic Framework"
                4 -> "Title: Key Technical Insights & Data Metrics"
                5 -> "Title: Implementation Roadmap & Milestones"
                6 -> "Title: Case Study Analysis & Key Takeaways"
                else -> "Title: Slide $i - Advanced In-Depth Analysis on ${request.topic}"
            }

            sb.append("---------------------------------------------------\n")
            sb.append("SLIDE $i / ${request.slideCount} | $title\n")
            sb.append("---------------------------------------------------\n")
            sb.append("• Key Point 1: Fundamental principles driving ${request.topic} in modern workflows.\n")
            sb.append("• Key Point 2: Comparative advantages and efficiency gains achieved.\n")
            sb.append("• Key Point 3: Strategic recommendations tailored for ${request.targetAudience}.\n\n")

            if (request.includeSpeakerNotes) {
                sb.append("🎤 SPEAKER NOTES:\n")
                sb.append("  \"Good morning everyone. On this slide, emphasize the strategic impact of ${request.topic}. Point out how data trends highlight measurable ROI for our audience.\"\n\n")
            }

            sb.append("🎨 VISUAL LAYOUT RECOMMENDATION:\n")
            sb.append("  Use a dual-column layout with Sapphire Blue highlight callout card on the right.\n\n")
        }

        return sb.toString()
    }

    fun getSampleInitialDocuments(): List<GeneratedDocument> {
        return listOf(
            GeneratedDocument(
                id = "doc-101",
                type = DocumentType.WORKSHEET,
                title = "Mathematics: Quadratic Equations & Graphing",
                subjectOrTopic = "Mathematics",
                gradeOrAudience = "Grade 9",
                itemCount = 12,
                createdAt = "Today, 10:15 AM",
                content = buildFallbackWorksheet(
                    WorksheetRequest("Mathematics", "Grade 9", "Quadratic Equations", "Mixed", 12, "Medium", true)
                ),
                isFavorite = true,
                statusBadge = "AI Ready"
            ),
            GeneratedDocument(
                id = "doc-102",
                type = DocumentType.PRESENTATION,
                title = "Smart City Urban Planning & Renewable Grids",
                subjectOrTopic = "Smart City Infrastructure",
                gradeOrAudience = "Executives & Policy Makers",
                itemCount = 10,
                createdAt = "Yesterday, 3:45 PM",
                content = buildFallbackPpt(
                    PptRequest("Smart City Urban Planning", "Executives", 10, "Minimal Sapphire Glass", true)
                ),
                isFavorite = false,
                statusBadge = "Pro Feature"
            ),
            GeneratedDocument(
                id = "doc-103",
                type = DocumentType.WORKSHEET,
                title = "Physics: Thermodynamics & Entropy Laws",
                subjectOrTopic = "Physics",
                gradeOrAudience = "Grade 11 AP",
                itemCount = 15,
                createdAt = "Sep 05, 2026",
                content = buildFallbackWorksheet(
                    WorksheetRequest("Physics", "Grade 11", "Thermodynamics", "Short Answer", 15, "Hard", true)
                ),
                isFavorite = true,
                statusBadge = "AI Ready"
            )
        )
    }

    private fun getCurrentDateFormatted(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
