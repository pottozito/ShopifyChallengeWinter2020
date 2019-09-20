package com.example.shopifychallenge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_layout.view.*

class ProductsAdapter(val products: ArrayList<Product>) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

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
            val oa1 = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0f, 1f)
            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    //imageView.setImageResource(android.R.drawable.frontSide)
                    oa2.start()
                }
            })
            oa1.start()
        }
    }

    /**
     * Return the size of the list
     */
    override fun getItemCount(): Int {
        return products.size
    }

    /**
     * Holds the custom list view
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(product: Product) {
            itemView.title.text=product.title
            itemView.image.setImageBitmap(product.imgBitmap)
        }
    }
}