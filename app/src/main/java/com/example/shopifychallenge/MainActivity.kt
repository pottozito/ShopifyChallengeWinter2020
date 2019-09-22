package com.example.shopifychallenge

import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var gameProductsAdapter: ProductsAdapter

    val productArray = ArrayList<Product>()
    val gameProductsArray = ArrayList<Product>()

    var cards = 20
    var matches = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 8)
        gameProductsAdapter = ProductsAdapter(gameProductsArray, recyclerView)
        recyclerView.adapter = gameProductsAdapter

        val loadingPanel = findViewById<RelativeLayout>(R.id.loadingPanel)

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
                loadingPanel.visibility = View.GONE
                Shuffle(cards)
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
            ShowSettingsDialog()
            true
        }
        R.id.action_shuffle ->{
            Shuffle(cards)
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

        for (i in 0 until numberOfCards/matches) {
            for (j in 0 until matches) {
                gameProductsArray.add(Product(productArray[i].id, productArray[i].title, productArray[i].imgBitmap))
            }
        }

        gameProductsArray.shuffle()
        gameProductsAdapter.notifyDataSetChanged()
    }

    fun ShowSettingsDialog() {
        val builder = AlertDialog.Builder(this)

        val customLayout = layoutInflater.inflate(R.layout.settings_dialog, null)
        builder.setView(customLayout)

        val matchesSeekBar = customLayout.findViewById<SeekBar>(R.id.matchesSeekBar)
        val matchesText = customLayout.findViewById<TextView>(R.id.matchesText)

        val cardsSeekBar = customLayout.findViewById<SeekBar>(R.id.cardsSeekBar)
        val cardsText = customLayout.findViewById<TextView>(R.id.cardsText)

        matchesSeekBar.min = 2
        matchesSeekBar.max = 4
        matchesSeekBar.progress = matches
        matchesSeekBar.incrementProgressBy(1)

        matchesText.text = Integer.toString(matches)

        matchesSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, i: Int, b: Boolean) {
                matchesText.text = Integer.toString(i)
                cardsSeekBar.max = 0
                cardsSeekBar.max = 50 * matchesSeekBar.progress
                cardsSeekBar.min = matchesSeekBar.progress
                cardsSeekBar.progress = matchesSeekBar.progress
                cardsSeekBar.incrementProgressBy(matchesSeekBar.progress)
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }
        })

        cardsSeekBar.min = matchesSeekBar.progress
        cardsSeekBar.max = 50 * matchesSeekBar.progress
        cardsSeekBar.setProgress(cards)
        cardsSeekBar.incrementProgressBy(matchesSeekBar.progress)

        cardsText.text = Integer.toString(matchesSeekBar.progress)

        cardsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, i: Int, b: Boolean) {
                var progress = i / matchesSeekBar.progress;
                progress = progress * matchesSeekBar.progress;
                cardsText.text = Integer.toString(progress)
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }
        })

        builder.setTitle("Customize New Game")
        builder.setPositiveButton("Shuffle!") { _, _ ->
            cards = cardsSeekBar.progress
            matches = matchesSeekBar.progress
            Shuffle(cards)
        }
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        val dialog = builder.create()
        dialog.show()
    }

    fun ShowVictory() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("You Win!")
        builder.setPositiveButton("Play Again") { _, _ ->
            Shuffle(cards)
        }
        builder.setNegativeButton("View Board") { _, _ ->

        }
        val dialog = builder.create()
        dialog.show()
    }
}
