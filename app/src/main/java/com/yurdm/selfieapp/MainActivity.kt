package com.yurdm.selfieapp

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {
    private val emailReceiver = arrayOf("hodovychenko.labs@gmail.com")
    private val emailSubject = "КПП АИ-193 Дмитриев"
    private var emailImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selfieBtn: Button = findViewById(R.id.selfieBtn)
        val sendBtn: Button = findViewById(R.id.sendBtn)
        val preview: ImageView = findViewById(R.id.preview)
        var image: Uri? = null

        val getCameraImage =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    preview.setImageURI(image)
                    emailImage = image
                }
            }

        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    image = getTempUri()
                    getCameraImage.launch(image)
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }

        selfieBtn.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        sendBtn.setOnClickListener {
            if (emailImage == null) {
                Toast.makeText(this, "Take a selfie before sending", Toast.LENGTH_SHORT).show()
            } else {
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "application/octet-stream"
                sendIntent.putExtra(Intent.EXTRA_EMAIL, emailReceiver)
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                sendIntent.putExtra(Intent.EXTRA_STREAM, emailImage)

                startActivity(sendIntent)
            }
        }
    }

    private fun getTempUri(): Uri {
        val tempFile =
            File.createTempFile("tmp", ".jpg", getExternalFilesDir("tmp_images")).apply {
                createNewFile()
                deleteOnExit()
            }

        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tempFile
        )
    }
}