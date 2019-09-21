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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_layout.view.*

class ProductsAdapter(val products: ArrayList<Product>, val recyclerView: RecyclerView) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    var pick1 = -1
    var pick2 = -1

    /**
     * Return the view for each item in the list
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    /**
     * Bind the data on the list
     */
    override fun onBindViewHolder(holder: ProductsAdapter.ViewHolder, position: Int) {
        holder.bindItems(products[holder.adapterPosition])

        holder.itemView.setOnClickListener {
            if (holder.itemView.tag == "back") {
                flipCardToFront(holder.itemView)
                if (pick1 == -1 && pick2 == -1) {
                    pick1 = holder.adapterPosition
                } else if (pick2 == -1) {
                    pick2 = holder.adapterPosition
                }
            }

            if (pick1 != -1 && pick2 != -1) {
                if (products[pick1].id == products[pick2].id) {

                } else {
                    flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick1)!!.itemView)
                    flipCardToBack(recyclerView.findViewHolderForAdapterPosition(pick2)!!.itemView)
                }
                pick1 = -1
                pick2 = -1
            }
        }
    }

    /**
     * Return the size of the list
     */
    override fun getItemCount(): Int {
        return products.size
    }

    fun flipCardToFront(view: View) {
        val oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        oa1.duration = 200
        oa2.duration = 200
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.tag = "front"
                view.title.visibility = View.VISIBLE
                view.image.visibility = View.VISIBLE
                view.backImage.visibility = View.GONE
                oa2.start()
            }
        })
        oa1.start()
    }

    fun flipCardToBack(view: View) {
        val oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        oa1.duration = 200
        oa2.duration = 200
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.tag = "back"
                view.backImage.visibility = View.VISIBLE
                view.title.visibility = View.GONE
                view.image.visibility = View.GONE
                oa2.start()
            }
        })
        oa1.startDelay = 1000
        oa1.start()
    }

    /**
     * Holds the custom list view
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(product: Product) {
            itemView.title.text=product.title
            itemView.image.setImageBitmap(product.imgBitmap)
            itemView.tag = "back"
        }
    }

}