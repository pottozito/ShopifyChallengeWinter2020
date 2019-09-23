package com.example.shopifychallenge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Handler
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.card_layout.view.*

class ProductsAdapter(val products: ArrayList<Product>, val recyclerView: RecyclerView) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    var pick1 = -1
    var pick2 = -1
    var pick3 = -1
    var pick4 = -1
    var cardsFlipped = 0
    var matches  = 0
    var matchesMade = 0
    var canFlip = true
    var score = 0

    //Return the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

     //Bind the data on the list
    override fun onBindViewHolder(holder: ProductsAdapter.ViewHolder, position: Int) {
        holder.bindItems(products[holder.adapterPosition])
        //On card click, check for matches and victory
        holder.itemView.setOnClickListener {
            if (products[holder.adapterPosition].side == "back" && canFlip) {
                matches = (holder.itemView.context as MainActivity).matches

                if (cardsFlipped == 0) {
                    pick1 = holder.adapterPosition
                    flipCardToFront(holder.itemView, products[holder.adapterPosition])
                } else if (cardsFlipped == 1) {
                    pick2 = holder.adapterPosition
                    flipCardToFront(holder.itemView, products[holder.adapterPosition])
                    if (matches == 2) {
                        canFlip = false
                        if (products[pick1].id == products[pick2].id) {
                            cardsFlipped = 0
                            Snackbar.make(holder.itemView, "Match Made!", Snackbar.LENGTH_SHORT).show()
                            matchesMade++
                            canFlip = true
                            score++
                            checkForWin(holder.itemView)
                        } else {
                            score--
                            flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick1)?.itemView, products[pick1])
                            flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick2)?.itemView, products[pick2])
                        }
                        (holder.itemView.context as MainActivity).scoreView.text = "Score :  $score"
                        pick1 = -1
                        pick2 = -1
                    }
                } else if (cardsFlipped == 2 && matches >= 3) {
                    pick3 = holder.adapterPosition
                    flipCardToFront(holder.itemView, products[holder.adapterPosition])

                    if (matches == 3) {
                        canFlip = false
                        if (products[pick1].id == products[pick2].id && products[pick2].id == products[pick3].id) {
                            cardsFlipped = 0
                            Snackbar.make(holder.itemView, "Match Made!", Snackbar.LENGTH_SHORT).show()
                            matchesMade++
                            canFlip = true
                            score++
                            checkForWin(holder.itemView)
                        } else {
                            score--
                            flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick1)?.itemView, products[pick1])
                            flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick2)?.itemView, products[pick2])
                            flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick3)?.itemView, products[pick3])
                        }
                        (holder.itemView.context as MainActivity).scoreView.text = "Score :  $score"
                        pick1 = -1
                        pick2 = -1
                        pick3 = -1
                    }
                } else if (cardsFlipped == 3 && matches == 4) {
                    pick4 = holder.adapterPosition
                    flipCardToFront(holder.itemView, products[holder.adapterPosition])
                    canFlip = false

                    if (products[pick1].id == products[pick2].id && products[pick2].id == products[pick3].id && products[pick3].id == products[pick4].id) {
                        cardsFlipped = 0
                        Snackbar.make(holder.itemView, "Match Made!", Snackbar.LENGTH_SHORT).show()
                        matchesMade++
                        score++
                        canFlip = true
                        checkForWin(holder.itemView)
                    } else {
                        score--
                        flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick1)?.itemView, products[pick1])
                        flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick2)?.itemView, products[pick2])
                        flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick3)?.itemView, products[pick3])
                        flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick4)?.itemView, products[pick4])
                    }
                    (holder.itemView.context as MainActivity).scoreView.text = "Score :  $score"
                    pick1 = -1
                    pick2 = -1
                    pick3 = -1
                    pick4 = -1
                }
            }
        }
    }

    //Reset for new board
    fun reset() {
        pick1 = -1
        pick2 = -1
        pick3 = -1
        pick4 = -1
        cardsFlipped = 0
        matchesMade = 0
        score = 0
        canFlip = true
    }

    //Return the size of the list
    override fun getItemCount(): Int {
        return products.size
    }

    //Flip card in given view to front
    fun flipCardToFront(view: View?, product: Product) {
        if (view != null) {
            val oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            oa1.duration = 200
            oa2.duration = 200
            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.title.visibility = View.VISIBLE
                    view.image.visibility = View.VISIBLE
                    view.backImage.visibility = View.GONE
                    oa2.start()
                }
            })
            oa1.start()
        }
        product.side = "front"
        cardsFlipped++
    }

    //Flip card in given view to back
    fun flipCardToBack(view: View?, product: Product) {
        if (view != null) {
            val oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            oa1.duration = 200
            oa2.duration = 200
            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    product.side = "back"
                    view.backImage.visibility = View.VISIBLE
                    view.title.visibility = View.GONE
                    view.image.visibility = View.GONE
                    canFlip = true
                    oa2.start()
                }
            })
            oa1.startDelay = 1000
            oa1.start()
        }
        cardsFlipped--
        product.side = "back"
    }

    //Check for a win based on number of matched cards
    fun checkForWin(itemView : View) {
        if (matchesMade == (itemView.context as MainActivity).cards/matches) {
            (itemView.context as MainActivity).ShowVictory(score)
        }
    }

     //Holds the custom list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(product: Product) {
            itemView.title.text=product.title
            itemView.image.setImageBitmap(product.imgBitmap)
            if (product.side == "back") {
                itemView.backImage.visibility = View.VISIBLE
                itemView.title.visibility = View.GONE
                itemView.image.visibility = View.GONE
            } else {
                itemView.backImage.visibility = View.GONE
                itemView.title.visibility = View.VISIBLE
                itemView.image.visibility = View.VISIBLE
            }
        }
    }

}