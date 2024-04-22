package com.example.quizonline

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizonline.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import android.view.View

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var quizModelList: MutableList<QuizModel>
    lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        getDataFromFirebase()

        // Get the ImageView
        val imageView: ImageView = findViewById(R.id.imageView_1)

        // Get the dimensions of the ImageView
        val targetWidth: Int = resources.displayMetrics.widthPixels
        val targetHeight: Int = (160 * resources.displayMetrics.density).toInt()

        // Get the resource id of the image
        val imageResId: Int = R.drawable.main_image

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeResource(resources, imageResId, options)

        val imageHeight: Int = options.outHeight
        val imageWidth: Int = options.outWidth
        val scaleFactor: Int = Math.min(imageWidth / targetWidth, imageHeight / targetHeight)

        // Decode the image file into a Bitmap sized to fill the View
        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor
        options.inPurgeable = true

        val bitmap = BitmapFactory.decodeResource(resources, imageResId, options)
        imageView.setImageBitmap(bitmap)
    }

    private fun setupRecyclerView() {
        binding.progressBar.visibility = View.GONE
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase(){
        binding.progressBar.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference
            .get()
            .addOnSuccessListener { dataSnapshot->
                if(dataSnapshot.exists()){
                    for (snapshot in dataSnapshot.children){
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            quizModelList.add(quizModel)
                        }
                    }
                }
                setupRecyclerView()
            }


    }
}