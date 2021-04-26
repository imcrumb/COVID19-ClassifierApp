package com.example.prototype

import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class Classifier constructor(
    private val assetManager: AssetManager,
    private val modelPath: String,
    private val labelPath: String
){

    private val MODEL = modelPath
    private val LABEL = labelPath

    private var interpreter:Interpreter?=null



    @Throws(RuntimeException::class)
    private fun loadLabelFile(asset: AssetManager, labelPath: String): Vector<String> {
        val labelList = Vector<String>()
        try {
            val br = BufferedReader(InputStreamReader(assetManager.open(LABEL)))
            val iter = br.lineSequence().iterator()
            while(iter.hasNext()){
                val line = iter.next()
                labelList.add(line)
            }
            br.close()
            return labelList
        }
        catch (e:IOException){
            throw RuntimeException(e)
        }
    }

    private fun loadModelFile(asset: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = asset.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}