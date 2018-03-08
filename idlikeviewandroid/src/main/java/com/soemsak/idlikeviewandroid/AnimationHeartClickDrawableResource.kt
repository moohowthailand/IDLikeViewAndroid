package com.soemsak.idlikeviewandroid

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat

class AnimationHeartClickDrawableResource private constructor(context: Context){

    private var imageIds:ArrayList<Int> = ArrayList()
    private var drawables:ArrayList<Drawable> = ArrayList()

    companion object {
        @Volatile private var instance : AnimationHeartClickDrawableResource? = null
        fun  getInstance(context: Context): AnimationHeartClickDrawableResource {
            if (instance == null) {
                instance = AnimationHeartClickDrawableResource(context)
            }
            return instance!!
        }
    }

    init {
        for (i in 1..75){
            var imageName = String.format("heartclick%d",i)
            val globeId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            imageIds.add(globeId)
            drawables.add(ContextCompat.getDrawable(context, globeId))
        }
    }

    fun getDrawables():ArrayList<Drawable> {
        return instance!!.drawables
    }

}