package com.example.shopifychallenge

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val productArray = ArrayList<Product>()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 5)
        productsAdapter = ProductsAdapter(productArray)
        recyclerView.adapter = productsAdapter

        doAsync {

            var jsonString = URL("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6").readText()
            val productsObject = JSONObject(jsonString)
            val productsArray = productsObject.getJSONArray("products")
            for (i in 0 until productsArray.length()) {
                val productObject = productsArray.getJSONObject(i)
                val imagesArray = productObject.getJSONArray("images")
                val imageSrc = imagesArray.getJSONObject(0).getString("src")
                val bitmap = BitmapFactory.decodeStream(URL(imageSrc).openConnection().getInputStream())
                Log.i("Test", imageSrc)
                productArray.add(Product(productObject.getInt("id"), productObject.getString("title"), bitmap))
            }
            uiThread {
                productsAdapter.notifyDataSetChanged()
            }
        }
    }
}
