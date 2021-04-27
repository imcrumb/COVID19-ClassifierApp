package com.example.prototype

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap


class MainActivity : AppCompatActivity() {

    private lateinit var classifier: Classifier
    private lateinit var bmpImage: Bitmap
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        classifier = Classifier(getAssets())

        val captureButton = findViewById<Button>(R.id.capture_button)
        captureButton.setOnClickListener {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 123)
        }

        val uploadButton = findViewById<Button>(R.id.upload_button)
        uploadButton.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 456)
        }

        val resultButton = findViewById<Button>(R.id.run_button)
        resultButton.setOnClickListener {

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
            var bitmap = imageFrame.drawable.toBitmap()
            bitmap = Bitmap.createScaledBitmap(bitmap,64,64,false)
            /*val stream = imageUri?.let { contentResolver!!.openInputStream(it) }
            //if (::imageUri.isInitialized) photoImage.recycle()
            bmpImage = BitmapFactory.decodeStream(stream)
            bmpImage = Bitmap.createScaledBitmap(bmpImage, 64, 64, false)
            imageFrame.setImageBitmap(bmpImage)*/
            classifier.recognise(bitmap)
        }

    }
}