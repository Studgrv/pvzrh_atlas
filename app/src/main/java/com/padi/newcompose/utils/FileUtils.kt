package com.padi.newcompose.utils
import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


object FileUtils {
    fun readAssetFile(context: Context, fileName: String): String? {
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
            reader.close()
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}