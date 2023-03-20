package com.example.uplaodpdftofirebasestorage.view

import android.app.DownloadManager
import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.uplaodpdftofirebasestorage.R
import com.example.uplaodpdftofirebasestorage.adapter.PdfAdapter
import com.example.uplaodpdftofirebasestorage.databinding.ActivityShowPdfFilesBinding
import com.example.uplaodpdftofirebasestorage.model.PdfInfo
import com.example.uplaodpdftofirebasestorage.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_show_pdf_files.*

class ShowPdfFiles : AppCompatActivity() {
    companion object {
        lateinit var storageReference :StorageReference
        lateinit var dM: DownloadManager
        lateinit var reference :StorageReference
        var pdfurll: String = ""

    }


    lateinit var mStorage: StorageReference
    private lateinit var swipeRefresh: SwipeRefreshLayout


    lateinit var binding: ActivityShowPdfFilesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPdfFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dM = getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

        mStorage = FirebaseStorage.getInstance().getReference("Upload")

//        swipeRefresh.setOnRefreshListener {
//            getData()
//        }

//        storage = FirebaseStorage.getInstance().reference.child("Uploads")
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
        val mm = ArrayList<PdfInfo>()
        FirebaseFirestore.getInstance().collection(Constants.PDF)
            .orderBy("pdfName", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (doc in documents) {
                        var id = doc.id
                        var pdfName = doc.get("pdfName").toString()
                        var pdfUrl = doc.get("pdfUrl").toString()
                        var categ = PdfInfo(id, pdfName, pdfUrl)
                        mm.add(categ)

                        var adapter =
                            PdfAdapter(this, mm)
                        pdfRecyclerView.adapter = adapter


//                    adapter.setOnItemClickListener(object : PdfAdapter.OnItemClickListener {
//                        @RequiresApi(Build.VERSION_CODES.M)
//                        override fun onItemClickListener(item: Int) {
//
//
//                        }
//                    }
//                    )
                        pdfRecyclerView.adapter!!.notifyDataSetChanged()
                    }
                }
            }


    }
}