package com.example.prototype

import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class Classifier constructor(
    private val assetManager: AssetManager,
    private val model_path: String,
    private val label_path: String
){

    private val MODEL_PATH = "model.tflite"
    private val LABEL_PATH = "label.txt"

    private var interpreter:Interpreter?=null
    private val labels = Vector<String>()


    private fun loadLabelFile(asset: AssetManager, labelPath: String): List<String> {

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