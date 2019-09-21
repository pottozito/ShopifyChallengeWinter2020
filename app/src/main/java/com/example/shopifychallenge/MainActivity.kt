package com.example.shopifychallenge

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var gameProductsAdapter: ProductsAdapter

    val productArray = ArrayList<Product>()
    val gameProductsArray = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 5)
        gameProductsAdapter = ProductsAdapter(gameProductsArray, recyclerView)
        recyclerView.adapter = gameProductsAdapter

        setSupportActionBar(findViewById(R.id.toolbar))

        doAsync {
            var jsonString = URL("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6").readText()
            val productsObject = JSONObject(jsonString)
            val productsArray = productsObject.getJSONArray("products")
            for (i in 0 until productsArray.length()) {
                val productObject = productsArray.getJSONObject(i)
                val imagesArray = productObject.getJSONArray("images")
                val imageSrc = imagesArray.getJSONObject(0).getString("src")
                val bitmap = BitmapFactory.decodeStream(URL(imageSrc).openConnection().getInputStream())
                productArray.add(Product(productObject.getInt("id"), productObject.getString("title"), bitmap))
            }
            uiThread {
                Shuffle(20)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Print" item
            Toast.makeText(this,"Settings action",Toast.LENGTH_LONG).show()
            true
        }
        R.id.action_shuffle ->{
            Shuffle(10)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun Shuffle(numberOfCards : Int) {

        val randomProducts = ArrayList<Int>()

        gameProductsAdapter.reset()

        gameProductsArray.clear()
        productArray.shuffle()

        for (i in 0 until productArray.size) {
            randomProducts.add(i)
        }

        randomProducts.shuffle()

        for (i in 0 until numberOfCards/2) {
            gameProductsArray.add(Product(productArray[i].id, productArray[i].title, productArray[i].imgBitmap))
            gameProductsArray.add(Product(productArray[i].id, productArray[i].title, productArray[i].imgBitmap))
        }

        gameProductsArray.shuffle()
        gameProductsAdapter.notifyDataSetChanged()
    }
}
