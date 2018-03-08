package com.soemsak.idlikeviewandroid

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat

class AnimationHeartDrawableResource private constructor(context: Context){

    private var imageIds:ArrayList<Int> = ArrayList()
    private var drawables:ArrayList<Drawable> = ArrayList()

    companion object {
        @Volatile private var instance : AnimationHeartDrawableResource? = null
        fun  getInstance(context: Context): AnimationHeartDrawableResource {
            if (instance == null) {
                instance = AnimationHeartDrawableResource(context)
            }
            return instance!!
        }
    }

    init {
        for (i in 0..179){
            var imageName = String.format("heartpumping%05d",i)
            val globeId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            imageIds.add(globeId)
            drawables.add(ContextCompat.getDrawable(context, globeId))
        }
    }

    fun getDrawables():ArrayList<Drawable> {
        return instance!!.drawables
    }

}