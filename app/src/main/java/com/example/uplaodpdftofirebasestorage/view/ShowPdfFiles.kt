package com.example.uplaodpdftofirebasestorage.view

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.uplaodpdftofirebasestorage.adapter.PdfAdapter
import com.example.uplaodpdftofirebasestorage.databinding.ActivityShowPdfFilesBinding
import com.example.uplaodpdftofirebasestorage.model.PdfInfo
import com.example.uplaodpdftofirebasestorage.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_show_pdf_files.*

class ShowPdfFiles : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    lateinit var db: FirebaseFirestore

    companion object {
        lateinit var mStorage: StorageReference
      //  lateinit var downloadManager: DownloadManager
        lateinit var reference: StorageReference
      //  var uriPdf: String = ""
    }


    //lateinit var mStorage: StorageReference
    private lateinit var swipeRefresh: SwipeRefreshLayout


    lateinit var binding: ActivityShowPdfFilesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPdfFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore


//        swipeRefresh.setOnRefreshListener {
//            getData()
//        }
        pdfRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@ShowPdfFiles
            )
        }
        getData()
    }


    private fun getData() {
//                if (swipeRefresh.isRefreshing) {
//            swipeRefresh.isRefreshing = false
//        }
        val pdfData = ArrayList<PdfInfo>()
        db.collection("pdfNames")
            .orderBy("pdfName", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (doc in documents) {
                        var id = doc.id
                        var pdfName = doc.get("pdfName").toString()
                        var categ = PdfInfo(
                            id, pdfName)
                        pdfData.add(categ)
                        var adapter =
                            PdfAdapter(this, pdfData)
                        pdfRecyclerView.adapter = adapter

                        pdfRecyclerView.adapter!!.notifyDataSetChanged()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents.", Toast.LENGTH_SHORT).show()
            }


    }
}
