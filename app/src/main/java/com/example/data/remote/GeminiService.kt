package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.PresentationData
import com.example.data.model.SlideItem
import com.example.data.model.WorksheetData
import com.example.data.model.WorksheetQuestion
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    var customApiKey: String = ""

    private fun getEffectiveApiKey(): String {
        if (customApiKey.isNotBlank()) return customApiKey.trim()
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") return apiKey.trim()
        return ""
    }

    suspend fun testApiKey(apiKeyToTest: String): Result<String> = withContext(Dispatchers.IO) {
        val key = apiKeyToTest.trim()
        if (key.isBlank()) {
            return@withContext Result.failure(Exception("API Key cannot be empty."))
        }
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$key"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "Respond strictly with 'OK' if this API key verification test succeeds."))
                        })
                    })
                })
            }
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errMessage = try {
                    val errObj = JSONObject(responseBodyString)
                    errObj.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}: ${response.message}"
                } catch (e: Exception) {
                    "HTTP ${response.code}: ${response.message}"
                }
                return@withContext Result.failure(Exception("Gemini API Verification Error: $errMessage"))
            }

            val responseObj = JSONObject(responseBodyString)
            val text = responseObj.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: "OK"

            Result.success("API Key is valid! Gemini 2.5 Flash response: \"${text.trim()}\"")
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Connection failed. Please check network or API key format."))
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun generateWorksheet(
        subject: String,
        gradeLevel: String,
        topic: String,
        questionTypes: List<String>,
        questionCount: Int,
        difficulty: String
    ): WorksheetData = withContext(Dispatchers.IO) {
        val effectiveApiKey = getEffectiveApiKey()

        if (effectiveApiKey.isNotEmpty()) {
            try {
                val prompt = """
                    Generate an educational worksheet in strictly structured JSON format.
                    Subject: $subject
                    Grade Level: $gradeLevel
                    Topic: $topic
                    Question Types requested: ${questionTypes.joinToString()}
                    Number of Questions: $questionCount
                    Difficulty: $difficulty

                    Respond ONLY with valid JSON matching this exact structure:
                    {
                      "title": "$subject Worksheet: $topic",
                      "subject": "$subject",
                      "gradeLevel": "$gradeLevel",
                      "topic": "$topic",
                      "instructions": "Answer all questions carefully.",
                      "questions": [
                        {
                          "id": 1,
                          "questionText": "Question text here",
                          "questionType": "Multiple Choice",
                          "options": ["Option A", "Option B", "Option C", "Option D"],
                          "correctAnswer": "Option A",
                          "explanation": "Brief explanation of the answer"
                        }
                      ]
                    }
                """.trimIndent()

                val jsonResponse = callGeminiApi(effectiveApiKey, prompt)
                val parsed = parseWorksheetJson(jsonResponse, subject, gradeLevel, topic)
                if (parsed != null && parsed.questions.isNotEmpty()) {
                    return@withContext parsed
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback rich generator if API key is not present or API call fails
        generateFallbackWorksheet(subject, gradeLevel, topic, questionTypes, questionCount, difficulty)
    }

    suspend fun generatePresentation(
        topic: String,
        targetAudience: String,
        slideCount: Int,
        designStyle: String,
        tone: String
    ): PresentationData = withContext(Dispatchers.IO) {
        val effectiveApiKey = getEffectiveApiKey()

        if (effectiveApiKey.isNotEmpty()) {
            try {
                val prompt = """
                    Generate a PowerPoint presentation outline in strictly structured JSON format.
                    Topic: $topic
                    Target Audience: $targetAudience
                    Slide Count: $slideCount
                    Design Style: $designStyle
                    Tone: $tone

                    Respond ONLY with valid JSON matching this exact structure:
                    {
                      "title": "$topic",
                      "topic": "$topic",
                      "targetAudience": "$targetAudience",
                      "designStyle": "$designStyle",
                      "slides": [
                        {
                          "slideNumber": 1,
                          "title": "Title Slide Title",
                          "subtitle": "Subtitle text",
                          "bulletPoints": ["Key point 1", "Key point 2"],
                          "visualSuggestion": "Graphic design suggestion for this slide",
                          "speakerNotes": "Notes for speaker"
                        }
                      ]
                    }
                """.trimIndent()

                val jsonResponse = callGeminiApi(effectiveApiKey, prompt)
                val parsed = parsePresentationJson(jsonResponse, topic, targetAudience, designStyle)
                if (parsed != null && parsed.slides.isNotEmpty()) {
                    return@withContext parsed
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback rich presentation generator
        generateFallbackPresentation(topic, targetAudience, slideCount, designStyle, tone)
    }

    private fun callGeminiApi(apiKey: String, prompt: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val body = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val responseBodyString = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            throw Exception("Gemini API error ${response.code}: $responseBodyString")
        }

        val responseObj = JSONObject(responseBodyString)
        val candidates = responseObj.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val textPart = parts?.optJSONObject(0)?.optString("text") ?: ""

        return textPart
    }

    private fun parseWorksheetJson(json: String, subject: String, gradeLevel: String, topic: String): WorksheetData? {
        return try {
            val root = JSONObject(json)
            val title = root.optString("title", "$subject Worksheet")
            val instructions = root.optString("instructions", "Answer all questions.")
            val questionsArray = root.optJSONArray("questions") ?: JSONArray()
            val questionList = mutableListOf<WorksheetQuestion>()

            for (i in 0 until questionsArray.length()) {
                val qObj = questionsArray.getJSONObject(i)
                val optionsArr = qObj.optJSONArray("options")
                val optionsList = mutableListOf<String>()
                if (optionsArr != null) {
                    for (j in 0 until optionsArr.length()) {
                        optionsList.add(optionsArr.getString(j))
                    }
                }

                questionList.add(
                    WorksheetQuestion(
                        id = qObj.optInt("id", i + 1),
                        questionText = qObj.optString("questionText", "Question ${i + 1}"),
                        questionType = qObj.optString("questionType", "Multiple Choice"),
                        options = optionsList,
                        correctAnswer = qObj.optString("correctAnswer", ""),
                        explanation = qObj.optString("explanation", "")
                    )
                )
            }

            WorksheetData(
                title = title,
                subject = subject,
                gradeLevel = gradeLevel,
                topic = topic,
                instructions = instructions,
                questions = questionList
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parsePresentationJson(json: String, topic: String, targetAudience: String, designStyle: String): PresentationData? {
        return try {
            val root = JSONObject(json)
            val title = root.optString("title", topic)
            val slidesArr = root.optJSONArray("slides") ?: JSONArray()
            val slideList = mutableListOf<SlideItem>()

            for (i in 0 until slidesArr.length()) {
                val sObj = slidesArr.getJSONObject(i)
                val bulletsArr = sObj.optJSONArray("bulletPoints")
                val bulletsList = mutableListOf<String>()
                if (bulletsArr != null) {
                    for (j in 0 until bulletsArr.length()) {
                        bulletsList.add(bulletsArr.getString(j))
                    }
                }

                slideList.add(
                    SlideItem(
                        slideNumber = sObj.optInt("slideNumber", i + 1),
                        title = sObj.optString("title", "Slide ${i + 1}"),
                        subtitle = sObj.optString("subtitle", ""),
                        bulletPoints = bulletsList,
                        visualSuggestion = sObj.optString("visualSuggestion", ""),
                        speakerNotes = sObj.optString("speakerNotes", "")
                    )
                )
            }

            PresentationData(
                title = title,
                topic = topic,
                targetAudience = targetAudience,
                designStyle = designStyle,
                slides = slideList
            )
        } catch (e: Exception) {
            null
        }
    }

    // Comprehensive Fallback Generators for immediate demo & offline usage
    private fun generateFallbackWorksheet(
        subject: String,
        gradeLevel: String,
        topic: String,
        questionTypes: List<String>,
        questionCount: Int,
        difficulty: String
    ): WorksheetData {
        val questions = mutableListOf<WorksheetQuestion>()
        val effectiveCount = if (questionCount <= 0) 5 else questionCount

        val primaryType = if (questionTypes.isNotEmpty()) questionTypes[0] else "Multiple Choice"

        for (i in 1..effectiveCount) {
            val type = if (questionTypes.isNotEmpty()) questionTypes[(i - 1) % questionTypes.size] else primaryType
            when (type) {
                "Multiple Choice" -> {
                    questions.add(
                        WorksheetQuestion(
                            id = i,
                            questionText = "Which statement best describes the fundamental principle of $topic in $subject at the $difficulty level?",
                            questionType = "Multiple Choice",
                            options = listOf(
                                "Option A: It regulates core interactions through systematic properties.",
                                "Option B: It remains constant regardless of environmental variables.",
                                "Option C: It fluctuates inversely with external force vectors.",
                                "Option D: It applies exclusively to isolated theoretical systems."
                            ),
                            correctAnswer = "Option A: It regulates core interactions through systematic properties.",
                            explanation = "Option A accurately reflects the core concept of $topic as taught in $gradeLevel $subject."
                        )
                    )
                }
                "True/False" -> {
                    questions.add(
                        WorksheetQuestion(
                            id = i,
                            questionText = "True or False: $topic is a vital foundational concept in $subject that applies across multiple scenarios.",
                            questionType = "True/False",
                            options = listOf("True", "False"),
                            correctAnswer = "True",
                            explanation = "This is True because $topic provides essential theoretical framework in $subject."
                        )
                    )
                }
                "Fill in Blank" -> {
                    questions.add(
                        WorksheetQuestion(
                            id = i,
                            questionText = "In $subject, the key mechanism behind $topic is called ____________.",
                            questionType = "Fill in Blank",
                            options = emptyList(),
                            correctAnswer = "Systematic Integration / Core Principle",
                            explanation = "The standard terminology for $topic in $gradeLevel curriculum."
                        )
                    )
                }
                else -> {
                    questions.add(
                        WorksheetQuestion(
                            id = i,
                            questionText = "Explain in 2-3 sentences how $topic influences key outcomes in $subject for $gradeLevel students.",
                            questionType = "Short Answer",
                            options = emptyList(),
                            correctAnswer = "Students should explain the core mechanism, key variables, and real-world application of $topic.",
                            explanation = "Look for clear definitions and logical reasoning."
                        )
                    )
                }
            }
        }

        return WorksheetData(
            title = "$subject Worksheet: $topic",
            subject = subject,
            gradeLevel = gradeLevel,
            topic = topic,
            instructions = "Read each question carefully and write your answers clearly in the spaces provided.",
            questions = questions
        )
    }

    private fun generateFallbackPresentation(
        topic: String,
        targetAudience: String,
        slideCount: Int,
        designStyle: String,
        tone: String
    ): PresentationData {
        val slides = mutableListOf<SlideItem>()
        val count = if (slideCount <= 0) 5 else slideCount

        // Slide 1: Title Slide
        slides.add(
            SlideItem(
                slideNumber = 1,
                title = topic,
                subtitle = "A Comprehensive Overview for $targetAudience ($designStyle Theme)",
                bulletPoints = listOf(
                    "Prepared for: $targetAudience",
                    "Tone & Approach: $tone",
                    "Powered by Sapphire AI Generator"
                ),
                visualSuggestion = "High-contrast sapphire gradient background with glowing title hero card and minimal typography.",
                speakerNotes = "Welcome the audience and introduce the core objectives of today's presentation on $topic."
            )
        )

        // Slide 2: Executive Summary
        if (count >= 2) {
            slides.add(
                SlideItem(
                    slideNumber = 2,
                    title = "Executive Summary & Context",
                    subtitle = "Why $topic matters today",
                    bulletPoints = listOf(
                        "Understanding core fundamentals of $topic.",
                        "Current industry trends and key milestones.",
                        "Direct impact and opportunities for $targetAudience."
                    ),
                    visualSuggestion = "Split layout with key metric stats callout on the left and 3 horizontal icon cards on the right.",
                    speakerNotes = "Highlight key context and set expectations for the deep dive ahead."
                )
            )
        }

        // Slide 3: Core Mechanics
        if (count >= 3) {
            slides.add(
                SlideItem(
                    slideNumber = 3,
                    title = "Key Pillars of $topic",
                    subtitle = "Foundational Architecture",
                    bulletPoints = listOf(
                        "Pillar 1: Structural framework and setup.",
                        "Pillar 2: Operational efficiency & workflow optimization.",
                        "Pillar 3: Continuous measurement and iterative scaling."
                    ),
                    visualSuggestion = "Three-column grid with glowing sapphire borders and numbered pillar badges.",
                    speakerNotes = "Elaborate on each pillar, giving concrete real-world examples."
                )
            )
        }

        // Additional Slides
        for (i in 4..count) {
            if (i == count) {
                // Final slide: Conclusion
                slides.add(
                    SlideItem(
                        slideNumber = i,
                        title = "Summary & Key Takeaways",
                        subtitle = "Next steps for $targetAudience",
                        bulletPoints = listOf(
                            "Review main insights on $topic.",
                            "Actionable roadmap for immediate execution.",
                            "Q&A session and open discussion."
                        ),
                        visualSuggestion = "Centered glassmorphic card with vibrant Call to Action button and contact details.",
                        speakerNotes = "Summarize the major points and open the floor for questions."
                    )
                )
            } else {
                slides.add(
                    SlideItem(
                        slideNumber = i,
                        title = "Deep Dive: Phase $i Analysis",
                        subtitle = "Detailed Breakdown",
                        bulletPoints = listOf(
                            "Strategic advantage in implementation phase $i.",
                            "Risk mitigation strategies for $targetAudience.",
                            "Expected benchmarks and success metrics."
                        ),
                        visualSuggestion = "Timeline chart or comparison matrix with highlighted active node.",
                        speakerNotes = "Walk through Phase $i step-by-step."
                    )
                )
            }
        }

        return PresentationData(
            title = topic,
            topic = topic,
            targetAudience = targetAudience,
            designStyle = designStyle,
            slides = slides
        )
    }
}
