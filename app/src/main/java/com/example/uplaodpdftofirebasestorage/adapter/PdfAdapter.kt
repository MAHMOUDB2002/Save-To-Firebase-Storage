package com.example.uplaodpdftofirebasestorage.adapter

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.uplaodpdftofirebasestorage.R
import com.example.uplaodpdftofirebasestorage.model.PdfInfo
import com.example.uplaodpdftofirebasestorage.utils.Constants
import com.example.uplaodpdftofirebasestorage.view.ShowPdfFiles
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.pdf_item.view.*

class PdfAdapter(
    //  private var mListener: OnItemClickListener,
    private val context: Context,
    private val AddToRecycler: ArrayList<PdfInfo>,
    //val click:OnItemClickListener
) : RecyclerView.Adapter<PdfAdapter.PdfInfoViewHolder>() {
    lateinit var pdfurll: String
    lateinit var db: FirebaseFirestore

    class PdfInfoViewHolder(
        itemView: View
        // , listener: PdfAdapter.OnItemClickListener
    ) :
        RecyclerView.ViewHolder(itemView) {
        val pdfName: TextView = itemView.findViewById(R.id.pdfName)
        val pdfUrl: TextView = itemView.findViewById(R.id.pdfUrl)
        // val pdfDownload: ImageView = itemView.findViewById(R.id.btnDownloadFile)

    }


//    interface OnItemClickListener {
//        fun onItemClickListener(item: String)
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfInfoViewHolder {

        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.pdf_item,
                    parent, false
                )
        return PdfInfoViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: PdfInfoViewHolder, position: Int) {

        val addPdfFile = AddToRecycler[position]
        holder.pdfName.text = AddToRecycler[position].pdfName.toString()
        holder.pdfUrl.text = AddToRecycler[position].pdfUrl.toString()

        val url = AddToRecycler[position].pdfUrl.toString()

        // holder.pdfDownload.text = AddToRecycler[position].pdfDownloaad.toString()
//        holder.uAddress.text = AddToRecycler[position].uAddress.toString()
//        holder.pdfDownload.setOnClickListener {
//            click.onItemClickListener(AddToRecycler.get(position).pdfUrl!!)
//        }


//        ShowPdfFiles.reference =
//            ShowPdfFiles.storageReference.child("$url")
//        ShowPdfFiles.reference.downloadUrl.addOnSuccessListener {
//            pdfurll = it.toString()
//        }
//            .addOnFailureListener {
//                Toast.makeText(context, "Failed to download", Toast.LENGTH_SHORT).show()
//            }
//        holder.itemView.setOnClickListener {
//            Toast.makeText(context, "$$url", Toast.LENGTH_SHORT).show()
//            download(pdfurll, "$$url")
//            notifyItemChanged(holder.adapterPosition)
//        }



        holder.itemView.btnDeleteUser.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Item")
            builder.setMessage("Are you sure uoy want to remove PDF File?")

            builder.setPositiveButton("Confirm") { dialog, which ->
                FirebaseFirestore.getInstance().collection(Constants.PDF).document(addPdfFile.id!!)
                    .delete().addOnSuccessListener {
                        AddToRecycler.removeAt(position)
                        notifyDataSetChanged()
                    }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
                .show()
            notifyItemChanged(holder.adapterPosition)
        }
    }


    override fun getItemCount(): Int {
        return AddToRecycler.size
    }

    fun download(url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url + ""))
        request.setTitle(fileName)
        request.setMimeType("application/pdf")
        request.allowScanningByMediaScanner()
        request.setAllowedOverMetered(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        val dm = ShowPdfFiles.dM
        dm.enqueue(request)
    }
}


//fun downloadPdfFile(url: String, fileName: String) {
//    val request = DownloadManager.Request(Uri.parse(url + ""))
//    request.setTitle(fileName)
//    request.setMimeType("application/pdf")
//    request.allowScanningByMediaScanner()
//    request.setAllowedOverMetered(true)
//    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
//    val dm = getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
//    dm.enqueue(request)
//}

