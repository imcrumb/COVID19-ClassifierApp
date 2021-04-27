package com.example.prototype

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.ArrayList

class Classifier constructor(private val assetManager: AssetManager){

    private val MODEL = "model.tflite"
    private val LABEL = "label.txt"
    private val labels = Vector<String>()
    private lateinit var imgBytes : ByteBuffer
    private lateinit var labelOut: Array<ByteArray>
    private var interpreter:Interpreter?=null
    private val intValues by lazy { IntArray(64 * 64) }

    init {
        var labels = loadLabelFile(assetManager,LABEL)
        labelOut = Array(1) { ByteArray(labels.size) }
        interpreter = Interpreter(loadModelFile(assetManager, MODEL))
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        imgBytes = ByteBuffer.allocateDirect(1 * 64 * 64 * 3)
        imgBytes.order(ByteOrder.nativeOrder())
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixel in intValues){
            var value = Color.red(pixel).toFloat()/255.0f
            imgBytes.putFloat(value)
        }
        return imgBytes
    }

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


    private fun getSortedArray(byteArray: Array<ByteArray>):ArrayList<Output>{
        val pq = PriorityQueue<Output>(3,
            Comparator<Output> { lhs, rhs ->
                java.lang.Float.compare(rhs.confidence!!, lhs.confidence!!)
            })
        for (i in labels.indices) {
            pq.add(Output("" + i, labels[i] , labelOut[0][i].toFloat()))
        }
        val outputs = ArrayList<Output>()
        val outputsSize = Math.min(pq.size, 3)
        for (i in 0 until outputsSize) {
            outputs.add(pq.poll())
        }
        return outputs

    }

    fun recognise(bitmap: Bitmap): ArrayList<Output>{
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap,64,64,false)
        val bBuf = bitmapToByteBuffer(scaledBitmap)
        interpreter?.run(bBuf,labelOut)
        return getSortedArray(labelOut)
    }

    fun close(){
        interpreter?.close()
    }

    class Output(var id: String?, var title: String?, var confidence: Float?)

}