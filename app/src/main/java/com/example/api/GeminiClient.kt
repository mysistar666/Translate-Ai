package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

    suspend fun translateSingle(
        text: String,
        sourceLang: String,
        targetLang: String,
        tone: String,
        correctGrammar: Boolean
    ): TranslationResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext TranslationResult.Error("API Key is not configured. Please add GEMINI_API_KEY in the AI Studio Secrets panel.")
        }

        // Build a highly tailored prompt based on options
        val isAuto = sourceLang.equals("Auto-Detect", ignoreCase = true)
        val srcContext = if (isAuto) "automatically detect the source language" else "the source language is $sourceLang"
        
        val systemInstruction = """
            You are a translation expert. Your task is to translate text accurately.
            $srcContext and the target language is $targetLang.
            Please translate using a '$tone' tone. Keep it natural and context-aware.
            
            ${if (correctGrammar) "Crucially: First correct any spelling or grammatical errors in the original text, and then translate the corrected text. Your output must follow the special format provided below." else "Do not output any introductory or explanatory text. Just output the direct translation."}
        """.trimIndent()

        val prompt = if (correctGrammar) {
            """
                Translate the following text. Since grammar correction is enabled, perform both grammatical correction and translation.
                
                Input text: "$text"
                
                You must respond EXACTLY in this format, and nothing else (split by a newline):
                Corrected: <Insert spelling and grammar-corrected text here. Keep original if already correct>
                Translation: <Insert target language translation here>
            """.trimIndent()
        } else {
            "Translate the following text into $targetLang using a '$tone' tone:\n\n$text"
        }

        try {
            val jsonPayload = buildPayload(prompt, systemInstruction)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody(mediaTypeJson))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API error: Code ${response.code}, Body: $errBody")
                    return@withContext TranslationResult.Error("API error ${response.code}: ID verification failed or model overloaded.")
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    return@withContext TranslationResult.Error("Received empty response from translation service.")
                }

                val outputText = parseSuccessText(responseBody)
                if (outputText.isEmpty()) {
                    return@withContext TranslationResult.Error("Failed to parse translation result.")
                }

                if (correctGrammar) {
                    // Try parsing "Corrected:" and "Translation:" lines
                    var corrected = text
                    var translation = outputText
                    val lines = outputText.lines()
                    for (line in lines) {
                        if (line.startsWith("Corrected:", ignoreCase = true)) {
                            corrected = line.substringAfter("Corrected:").trim().removeSurrounding("\"")
                        } else if (line.startsWith("Translation:", ignoreCase = true)) {
                            translation = line.substringAfter("Translation:").trim().removeSurrounding("\"")
                        }
                    }
                    if (translation == outputText && !outputText.contains("Translation:")) {
                        // fallback if Gemini ignored format
                        TranslationResult.Success(original = text, corrected = corrected, translated = outputText, detectedLang = if (isAuto) "Detected Language" else sourceLang)
                    } else {
                        TranslationResult.Success(original = text, corrected = corrected, translated = translation, detectedLang = if (isAuto) "Detected Language" else sourceLang)
                    }
                } else {
                    TranslationResult.Success(original = text, corrected = text, translated = outputText, detectedLang = if (isAuto) "Detected Language" else sourceLang)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network failure during translation", e)
            TranslationResult.Error("Network error: Please check your internet connection. (Details: ${e.localizedMessage})")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected translation exception", e)
            TranslationResult.Error("Unexpected error occurred: ${e.localizedMessage}")
        }
    }

    suspend fun translateMulti(
        text: String,
        sourceLang: String,
        targetLanguages: List<String>,
        tone: String
    ): MultiTranslationResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext MultiTranslationResult.Error("API Key is not configured. Please add GEMINI_API_KEY in the AI Studio Secrets panel.")
        }

        val isAuto = sourceLang.equals("Auto-Detect", ignoreCase = true)
        val srcContext = if (isAuto) "automatically detect the source language" else "the source language is $sourceLang"
        val targetsStr = targetLanguages.joinToString(", ")

        val systemInstruction = """
            You are an expert multi-language translator.
            $srcContext. Your task is to translate the input text into multiple languages simultaneously inside a single request.
            The target languages are: $targetsStr.
            Use a '$tone' tone for all translations.
            Ensure translations are of premium natural quality. Keep the output formatted exactly as requested.
        """.trimIndent()

        val prompt = """
            Translate the following text into each of these languages: $targetsStr.
            
            Text: "$text"
            
            Your response MUST contain each language name followed by its translation, with each on a new line. Format:
            [Language Name]: [Translation]
            
            Example response for targets 'Spanish, French':
            Spanish: Hola
            French: Bonjour
            
            Do not include any greeting, explanation, disclaimer, markdown bold wrappers like **Spanish**, or extra text. Only provide the list of translations.
        """.trimIndent()

        try {
            val jsonPayload = buildPayload(prompt, systemInstruction)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody(mediaTypeJson))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Multi API error: Code ${response.code}, Body: $errBody")
                    return@withContext MultiTranslationResult.Error("API error ${response.code}: Failed to translate.")
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    return@withContext MultiTranslationResult.Error("Received empty response.")
                }

                val rawOutput = parseSuccessText(responseBody)
                if (rawOutput.isEmpty()) {
                    return@withContext MultiTranslationResult.Error("No translations generated.")
                }

                // Parse out the languages
                val resultMap = mutableMapOf<String, String>()
                val lines = rawOutput.lines()
                for (line in lines) {
                    val trimmedLine = line.trim()
                    if (trimmedLine.isEmpty()) continue
                    
                    // Look for separator (either first ':' or '-' can act as indicator)
                    val firstColon = trimmedLine.indexOf(':')
                    if (firstColon > 0) {
                        val parsedLanguage = trimmedLine.substring(0, firstColon).trim().removeSurrounding("**").removeSurrounding("*")
                        val translation = trimmedLine.substring(firstColon + 1).trim().removeSurrounding("\"")
                        
                        // Check if parsedLanguage is one of our target languages
                        val matchedLang = targetLanguages.firstOrNull { it.equals(parsedLanguage, ignoreCase = true) } ?: parsedLanguage
                        resultMap[matchedLang] = translation
                    }
                }

                // Handle any targets that weren't captured gracefully (just in case)
                for (target in targetLanguages) {
                    if (!resultMap.containsKey(target)) {
                        // See if there is a partial line match
                        val linesMatching = lines.firstOrNull { it.contains(target, ignoreCase = true) }
                        if (linesMatching != null) {
                            val rightPart = linesMatching.substringAfter(target).trim().removePrefix(":").trim().removeSurrounding("\"")
                            resultMap[target] = rightPart
                        } else {
                            resultMap[target] = "Translation not returned in response."
                        }
                    }
                }

                MultiTranslationResult.Success(original = text, translations = resultMap)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network failure during multi-translation", e)
            MultiTranslationResult.Error("Network connection error. Please try again.")
        } catch (e: Exception) {
            Log.e(TAG, "Multi-translate Exception", e)
            MultiTranslationResult.Error("System Error: ${e.localizedMessage}")
        }
    }

    private fun buildPayload(prompt: String, systemInstruction: String): JSONObject {
        val root = JSONObject()
        val contents = JSONArray()
        val content = JSONObject()
        val parts = JSONArray()
        val part = JSONObject()
        
        part.put("text", prompt)
        parts.put(part)
        content.put("parts", parts)
        contents.put(content)
        root.put("contents", contents)

        val config = JSONObject()
        config.put("temperature", 0.3)
        root.put("generationConfig", config)

        val sysObj = JSONObject()
        val sysParts = JSONArray()
        val sysPart = JSONObject()
        sysPart.put("text", systemInstruction)
        sysParts.put(sysPart)
        sysObj.put("parts", sysParts)
        root.put("systemInstruction", sysObj)

        return root
    }

    private fun parseSuccessText(responseBody: String): String {
        return try {
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val contentObj = firstCandidate?.optJSONObject("content")
            val partsArray = contentObj?.optJSONArray("parts")
            val firstPartObj = partsArray?.optJSONObject(0)
            firstPartObj?.optString("text") ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse translation text error", e)
            ""
        }
    }
}

sealed interface TranslationResult {
    data class Success(
        val original: String,
        val corrected: String,
        val translated: String,
        val detectedLang: String
    ) : TranslationResult
    data class Error(val message: String) : TranslationResult
}

sealed interface MultiTranslationResult {
    data class Success(
        val original: String,
        val translations: Map<String, String> // Language -> Translated Text
    ) : MultiTranslationResult
    data class Error(val message: String) : MultiTranslationResult
}
