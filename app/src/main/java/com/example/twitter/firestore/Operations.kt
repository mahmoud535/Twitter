package com.example.twitter.firestore

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.util.zip.CheckedInputStream

class Operations {

    fun ConvertStreamToString(inputStream: InputStream):String{
        val bufferReader=BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String=""

        try {
            do{
                line=bufferReader.readLine()
                if (line!=null){
                    AllString+=line
                }
            }while (line!=null)
                inputStream.close()
        }catch (ex:Exception){}
        return AllString
    }
}