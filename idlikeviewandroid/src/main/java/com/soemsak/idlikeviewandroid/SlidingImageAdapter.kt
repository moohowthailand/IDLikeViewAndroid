package com.soemsak.idlikeviewandroid

import android.content.Context
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView


class SlidingImageAdapter: PagerAdapter {


    lateinit var imageUrls: ArrayList<String>
    lateinit var inflater: LayoutInflater
    lateinit var context: Context

    constructor(context: Context, imageUrls: ArrayList<String>){
        this.context = context
        this.imageUrls = imageUrls
        this.inflater = LayoutInflater.from(context)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view.equals(obj)
    }

    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.image_layout, view, false)!!

        val imageView = imageLayout.findViewById<ImageView>(R.id.imageView) as ImageView

//        imageView.setImageResource(IMAGES.get(position))
        if(position % 2 == 0){
            imageView.setBackgroundColor(Color.GREEN)
        }else{
            imageView.setBackgroundColor(Color.GRAY)
        }

//        imageView.setOnClickListener(object : View.OnClickListener {
//            fun onClick(v: View) {
//                //this will log the page number that was click
//                Log.i("TAG", "This page was clicked: " + pos)
//            }
//        })

        view.addView(imageLayout, 0)

        return imageLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

}