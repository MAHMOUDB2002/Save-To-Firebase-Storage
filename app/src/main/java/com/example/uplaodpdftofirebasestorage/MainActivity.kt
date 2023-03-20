package com.example.uplaodpdftofirebasestorage

import android.Manifest
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.uplaodpdftofirebasestorage.adapter.PdfAdapter
import com.example.uplaodpdftofirebasestorage.model.PdfInfo
import com.example.uplaodpdftofirebasestorage.utils.Constants
import com.example.uplaodpdftofirebasestorage.view.ShowPdfFiles
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI

// Run it
class MainActivity : AppCompatActivity()
// , PdfAdapter.OnItemClickListener
{
    val ReqCodePDF: Int = 0
    var pdfUri: Uri? = null
    lateinit var pdfName: String
    lateinit var mStorage: StorageReference
    private lateinit var progressDialog: ProgressDialog
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mStorage = FirebaseStorage.getInstance().getReference("Upload")

//        swipeRefresh.setOnRefreshListener {
//            getData()
//        }

//        storage = FirebaseStorage.getInstance().reference.child("Uploads")
//        pdfRecyclerView.apply {
//            layoutManager = LinearLayoutManager(
//                this@MainActivity
//            )
//        }
//
//        getData()


        showPdfFiles.setOnClickListener {
            val intent = Intent(this, ShowPdfFiles::class.java)
            startActivity(intent)
        }


        uploadPdf.setOnClickListener {
            if (uriText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please Enter PDF Name", Toast.LENGTH_LONG).show()
            } else {
                pdfName = uriText.text.toString()
                progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading....")
                progressDialog.setCancelable(false)
                progressDialog.setCanceledOnTouchOutside(false)
                //progressDialog.show()


                val intent = Intent()
                intent.setType("pdf/*")
                //  intent.setType("application/pdf")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select PDF File"), ReqCodePDF)


                // pdfRecyclerView.adapter?.notifyDataSetChanged()
            }
        }


    }
//
//    private fun getData() {
//
//        //        if (swipeRefresh.isRefreshing) {
////            swipeRefresh.isRefreshing = false
////        }
//        val mm = ArrayList<PdfInfo>()
//        FirebaseFirestore.getInstance().collection(Constants.PDF)
//            .orderBy("pdfName", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { documents ->
//                if (!documents.isEmpty) {
//                    for (doc in documents) {
//                        var id = doc.id
//                        var pdfName = doc.get("pdfName").toString()
//                        var pdfUrl = doc.get("pdfUrl").toString()
//                        var categ = PdfInfo(id, pdfName, pdfUrl)
//                        mm.add(categ)
//
//                        var adapter =
//                            PdfAdapter(this, mm)
//                        pdfRecyclerView.adapter = adapter
//
//
////                    adapter.setOnItemClickListener(object : PdfAdapter.OnItemClickListener {
////                        @RequiresApi(Build.VERSION_CODES.M)
////                        override fun onItemClickListener(item: Int) {
////
////
////                        }
////                    }
////                    )
//                        pdfRecyclerView.adapter!!.notifyDataSetChanged()
//                    }
//                }
//            }
//
//
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ReqCodePDF) {
                pdfUri = data!!.data!!
                upload()
                uriText.text.clear()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun upload() {
        progressDialog.setMessage("Uploading....")
        progressDialog.show()
        //  ref.child("$pdfName/").putFile(pdfUri!!)
        val timeStamp = System.currentTimeMillis().toString()
        val filePathAndName = "Upload/$timeStamp"
        val storageReference = FirebaseStorage.getInstance()
            .getReference(filePathAndName)
        storageReference
            //.child("$pdfName/")
            .putFile(pdfUri!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
//                        val hashMap: HashMap<String, String> = HashMap()
//                        hashMap.put("pdfUrl", uri.toString())


                        //                        val pdfInfo = hashMapOf(
                        //                            "pdfName" to pdfName,
                        ////                        "pdfUrl" to pdfUri
                        //                        )
                        val map = HashMap<String, Any>()
                        map["pdfUrl"] = uri.toString()
                        map["pdfName"] = pdfName

                        var db = FirebaseFirestore.getInstance()
                        db.collection(Constants.PDF)
                            .add(map)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Uploaded SuccessFully...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            .addOnFailureListener {
                                progressDialog.dismiss()
                                Toast.makeText(this, "Failed to Upload!!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
            }
    }


//    fun downloadPdfFile(url: String, fileName: String) {
//        val request = DownloadManager.Request(Uri.parse(url + ""))
//        request.setTitle(fileName)
//        request.setMimeType("application/pdf")
//        request.allowScanningByMediaScanner()
//        request.setAllowedOverMetered(true)
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
//        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//        dm.enqueue(request)
//    }


}


//                    private fun startDownloading() {
//                        val request = DownloadManager.Request(Uri.parse(pdfurll))
//                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//                        request.setTitle("Download")
//                        request.setDescription("Your File IS Downloading...")
//                        request.allowScanningByMediaScanner()
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        request.setDestinationInExternalPublicDir(
//                            Environment.DIRECTORY_DOWNLOADS,
//                            "${(System.currentTimeMillis())}"
//                        )
//                        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                        manager.enqueue(request)
//                    }
//
//                    @RequiresApi(Build.VERSION_CODES.M)
//                    override fun onItemClickListener(item: String) {
//                        pdfurll = item
//                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                            requestPermissions(
//                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                                ReqCodePDF
//                            )
//                        } else {
//                            startDownloading()
//                        }
//                        Toast.makeText(
//                            this@MainActivity,
//                            "You clicked on Download item",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }


// val down = mStorage.child("Uploads").getFile("Uploads")


//        var islandRef = mStorage.child("Uploads")
//
//        val localFile = File.createTempFile("Pdf", "pdf")
//
//        islandRef.getFile(localFile).addOnSuccessListener {
//            //loadText.setText(localFile.readText())
//        }.addOnFailureListener {
//            // Handle any errors
//        }

//        downloadPdf.setOnClickListener {
//            ref = storageReference.child("$pdfName/")
//            progressDialog = ProgressDialog(this)
//            progressDialog.setTitle("Loading....")
//            progressDialog.setCancelable(false)
//            progressDialog.setCanceledOnTouchOutside(false)
//
////            var num = 78
////            val sub = uriText.text.substring(num)
////            val mm = loadText.setText(sub)
//
//            ref.downloadUrl.addOnSuccessListener {
//                uriFrom = it.toString()
//                Toast.makeText(this, "Successfully Downloaded :)", Toast.LENGTH_LONG).show()
//
//            }.addOnFailureListener {
//                Toast.makeText(this, "Failed to Download", Toast.LENGTH_LONG).show()
//            }
//            download(uriFrom, "Download")
//        }


//                    pdfRecyclerView.adapter = PdfAdapter(this, mm)

//ref = storageReference.child("$pdfName/")


//                    ref.downloadUrl.addOnSuccessListener {
//                        uriFrom = it.toString()
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Start Downloading",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                        .addOnFailureListener {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Failed to Download",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }


// val m = loadText.setText("${pdfUri.toString()}")
//            download(loadText.text.toString(), "Download")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//
//                    requestPermissions(
//                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                        STORAGE_PER_CODE
//                    )
//                } else {
//
//                    startDownloading()
//                }
//
//            } else {
//
//            }


//fun saveToFireStore(){
//    storageReference.downloadUrl.addOnSuccessListener { uri ->
//        val pdfInfo = hashMapOf(
//            "pdfName" to pdfName,
////                        "pdfUrl" to pdfUri
//        )
//        val map = HashMap<String, Any>()
//        map["pdfUrl"] = uri.toString()
//
//        db.collection(Constants.PDF)
//            .add(map)
//            .addOnCompleteListener { firestoreTask->
//                if (firestoreTask.isSuccessful){
//                    progressDialog.dismiss()
//                    Toast.makeText(
//                        this,
//                        "Added SuccessFully...............",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }else{
//                    progressDialog.dismiss()
//                    Toast.makeText(this, "Failed to Add!!!!!!!!!!", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//    }
//}


//    private fun startDownloading() {
//
//        val url = uriText.text.toString()
//
//        val request = DownloadManager.Request(Uri.parse(url))
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//
//        request.setTitle("Download")
//        request.setDescription("The File isDownloading.....")
//        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(
//            Environment.DIRECTORY_DOWNLOADS,
//            "${System.currentTimeMillis()}"
//        )
//
//        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        manager.enqueue(request)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when (requestCode) {
//            STORAGE_PER_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startDownloading()
//                } else {
//                    Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }