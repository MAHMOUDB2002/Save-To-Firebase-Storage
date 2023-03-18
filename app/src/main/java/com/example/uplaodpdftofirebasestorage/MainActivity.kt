package com.example.uplaodpdftofirebasestorage

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI


class MainActivity : AppCompatActivity() {
    val PDF: Int = 0
    var pdfUri: Uri? = null
    val url: String = ""
    lateinit var mStorage: StorageReference
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mStorage = FirebaseStorage.getInstance().getReference("Uploads")

        uploadPdf.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading....")
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            //progressDialog.show()

            val intent = Intent()
            intent.setType("pdf/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF)
        }

        downloadPdf.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Loading....")
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            load()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PDF) {
                pdfUri = data!!.data!!
                uriText.text = pdfUri.toString()
                upload()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun upload() {
        progressDialog.setMessage("Uploading....")
        progressDialog.show()

        val timeStamp = System.currentTimeMillis()
        val filePathAndName = "Uploads/$timeStamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(this, "erorrrrrrrrrrr", Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
            }
    }

    private fun load() {
        progressDialog.setMessage("Loading from Storage....")
        progressDialog.show()

        val storageRef = FirebaseStorage.getInstance().getReference("Uploads")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val pdfUri = uri.toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(pdfUri), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            progressDialog.dismiss()
            Toast.makeText(this, "loaded successfully", Toast.LENGTH_LONG).show()


        }.addOnFailureListener { exception ->
            Log.e("PDF Viewer", "Error downloading PDF: $exception")
            progressDialog.dismiss()
            Toast.makeText(this, "erorrrrrrrrrrr", Toast.LENGTH_LONG).show()

        }
//        val ref = FirebaseStorage.getInstance().getReference("Uploads")
//        ref.metadata.addOnSuccessListener { storageMetaData ->
//            Toast.makeText(this, "Loaded Successfully", Toast.LENGTH_LONG).show()
//            val bytes = storageMetaData.sizeBytes.toDouble()
//            //convert bytes to KB/MB
//            val kb = bytes / 1024
//            val mb = kb / 1024
//            if (mb >= 1) {
//                loadText.text = "${String.format("$.2f", mb)} MB"
//            } else if (kb >= 1) {
//                loadText.text = "${String.format("$.2f", kb)} KB"
//            } else {
//                loadText.text = "${String.format("$.2f", bytes)} bytes"
//            }
//            progressDialog.dismiss()
//
//        }
//            .addOnFailureListener {
//                Toast.makeText(this, "Failed to get PDF", Toast.LENGTH_LONG).show()
//                progressDialog.dismiss()
//
//            }

    }
}