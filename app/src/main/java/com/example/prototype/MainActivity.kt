package com.example.prototype

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val captureButton = findViewById<Button>(R.id.capture_button)
        captureButton.setOnClickListener {
            val result = findViewById<TextView>(R.id.result_text)
            val judge = findViewById<TextView>(R.id.judge_text)
            result.text = ""
            judge.text=""
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 123)
        }

        val uploadButton = findViewById<Button>(R.id.upload_button)
        uploadButton.setOnClickListener {
            val result = findViewById<TextView>(R.id.result_text)
            val judge = findViewById<TextView>(R.id.judge_text)
            result.text = ""
            judge.text=""
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 456)
        }

        val resultButton = findViewById<Button>(R.id.run_button)
        resultButton.setOnClickListener {
            var rnds = (1..10).random()
            val result = findViewById<TextView>(R.id.result_text)
            val judge = findViewById<TextView>(R.id.judge_text)
            Thread.sleep(500)
            if (rnds>=5){
                result.text = "Fracture Confidence: 0.6"
                judge.text = "Confidence result is below preset value"
            }
            else{
                result.text = "Fracture Confidence: 0.2"
                judge.text = "Confidence result is below preset value"
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123)
        {
            var bmp = data?.extras?.get("data") as Bitmap
            val imageFrame = findViewById<ImageView>(R.id.main_image)
            imageFrame.setImageBitmap(bmp)
        }
        else if (requestCode==456)
        {
            val imageFrame = findViewById<ImageView>(R.id.main_image)
            imageFrame.setImageURI(data?.data)
        }

    }
}