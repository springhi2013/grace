package com.example.helloworld

import java.io.*
import android.content.Context


object AssetUtils {

    fun getFromAssets(context: Context, fileName: String): String {
        try {
            val inputReader = InputStreamReader(context.resources.getAssets().open(fileName))
            val bufReader = BufferedReader(inputReader)
            var line = ""
            var Result = ""
            line = bufReader.readLine()
            while (line != null) {
                Result += line
                line = bufReader.readLine()
            }
            return Result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    fun readRaw(context: Context, fileId: Int): String {
        try {
            //获取文件中的内容
            val inputStream = context.getResources().openRawResource(fileId)
            //将文件中的字节转换为字符
            val isReader = InputStreamReader(inputStream, "UTF-8")
            //使用bufferReader去读取字符
            val reader = BufferedReader(isReader)
            var line: String?
            var Result = ""
            line = reader.readLine()
            while (line != null) {
                Result += line
                line = reader.readLine()
            }
            return Result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}
