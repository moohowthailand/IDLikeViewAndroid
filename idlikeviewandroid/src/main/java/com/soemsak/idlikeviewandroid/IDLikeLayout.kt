package com.soemsak.idlikeviewandroid

import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.GestureDetector
import android.widget.RelativeLayout
import android.view.MotionEvent
import android.view.View
import java.util.*
import android.graphics.Canvas
import android.widget.ImageView
import android.widget.LinearLayout
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.StateListDrawable
import android.media.MediaPlayer
import android.os.Handler
import android.text.Layout
import android.util.Log
import android.view.animation.TranslateAnimation

class IDLikeLayout: RelativeLayout {

    lateinit var heartClickAnimationDrawable: AnimationDrawable
    lateinit var heartPumpingAnimationDrawable: AnimationDrawable

    var isLongPressLike = false

    val MAX_CLICK_DISTANCE = 15
    val LONG_PRESS_TIME = 300
    var MAX_CLICK_DURATION = 200
    var startClickTime: Long = 0
    lateinit var pager: ViewPager

    var pressStartTime: Long = 0
    var pressedX: Float = 0.toFloat()
    var pressedY: Float = 0.toFloat()

    var timeClicked: Long = 0
    var timeReleased: Long  = 0
    var longPressHandler = Handler()

    var paint = Paint()
    var path = Path()
    lateinit var gestureDetector: GestureDetector
    var heartPumpingAnimationSet:AnimatorSet = AnimatorSet()

    var heartPumpingImageView: ImageView? = null

//    var heartSoundEffect:MediaPlayer? = null
    var heartPumpingSoundEffect:MediaPlayer? = null

    constructor(context: Context) : super(context){
        init(context)
    }

    constructor(context:Context, attrs: AttributeSet) : super(context, attrs){
        init(context)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        if(heartPumpingImageView == null) {
            heartPumpingImageView = ImageView(context)
            heartPumpingImageView!!.setBackgroundColor(Color.GRAY)
            heartPumpingImageView!!.visibility = View.INVISIBLE
            prepareHeartPumpingImages(heartPumpingImageView!!)
            heartPumpingImageView!!.setBackgroundColor(Color.RED)
            heartPumpingImageView!!.scaleType = ImageView.ScaleType.CENTER_INSIDE;

            val params = RelativeLayout.LayoutParams(this.measuredWidth, (this.measuredWidth.toFloat() * 0.6).toInt())
            params.addRule(RelativeLayout.CENTER_HORIZONTAL)
            params.addRule(RelativeLayout.CENTER_VERTICAL)
            this@IDLikeLayout.addView(heartPumpingImageView, params)
        }
    }

    fun prepareHeartClickImages(likeStickerImageView: ImageView){
        likeStickerImageView.setBackgroundDrawable(null)
        heartClickAnimationDrawable = AnimationDrawable()
        heartClickAnimationDrawable.isOneShot = true
        for (i in 0..74) {
            heartClickAnimationDrawable.addFrame(AnimationHeartClickDrawableResource.getInstance(context).getDrawables()[i],2)
        }
        likeStickerImageView.setBackgroundDrawable(heartClickAnimationDrawable)
    }

    fun prepareHeartPumpingImages(likeStickerImageView: ImageView){
        likeStickerImageView.setBackgroundDrawable(null)
        heartPumpingAnimationDrawable = AnimationDrawable()
        heartPumpingAnimationDrawable.isOneShot = true
        for (i in 0..179) {
            heartPumpingAnimationDrawable.addFrame(AnimationHeartDrawableResource.getInstance(context).getDrawables()[i],40)
        }
        likeStickerImageView.setBackgroundDrawable(heartPumpingAnimationDrawable)
    }

    fun animateStartWhenHold() {
        heartPumpingImageView!!.visibility = View.VISIBLE
        heartPumpingAnimationDrawable.start()
    }

    fun init(context:Context) {
        this.setWillNotDraw(false)

        var rootView = inflate(context, R.layout.id_like_layout, this)
        pager = rootView.findViewById<ViewPager>(R.id.pager)
        var imageUrls = ArrayList<String>()
        imageUrls.add("https://google.com")
        imageUrls.add("https://google.com")
        imageUrls.add("https://google.com")
        pager.adapter = SlidingImageAdapter(context,imageUrls)


//        val params = RelativeLayout.LayoutParams(-1,-1)
//        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
//        params.addRule(RelativeLayout.CENTER_VERTICAL)
//        this@IDLikeLayout.addView(heartPumpingImageView, params)

        val _longPressed = Runnable {

            val fadeOut = ObjectAnimator.ofFloat(heartPumpingImageView, "alpha", 0.5f, 1f)
            fadeOut.duration = 200
            heartPumpingAnimationSet = AnimatorSet()
            heartPumpingAnimationSet.play(fadeOut)//.after(fadeOut)
            heartPumpingAnimationSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                }
            })
            heartPumpingAnimationSet.start()

            this@IDLikeLayout.heartPumpingImageView!!.setImageDrawable(StateListDrawable())
            heartPumpingImageView!!.setImageDrawable(StateListDrawable())
            prepareHeartPumpingImages(heartPumpingImageView!!)
            isLongPressLike = true
            heartPumpingImageView!!.visibility = View.VISIBLE
            this@IDLikeLayout.animateStartWhenHold()
            playPumping()

        }

        this.pager.setOnTouchListener(object: OnTouchListener{
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if(event.action == MotionEvent.ACTION_DOWN) {
                    timeClicked = Date().time
                    longPressHandler.postDelayed(_longPressed, LONG_PRESS_TIME.toLong())
                    pressStartTime = System.currentTimeMillis();
                    this@IDLikeLayout.pressedX = event.getX();
                    this@IDLikeLayout.pressedY = event.getY();
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                }else if(event.action == MotionEvent.ACTION_CANCEL){
                    longPressHandler.removeCallbacks(_longPressed)
                    if(isLongPressLike){
                        isLongPressLike = false
                        heartPumpingAnimationDrawable.stop()
//                        heartPumpingImageView.visibility = View.INVISIBLE
                    }
                }else if(event.action == MotionEvent.ACTION_UP){
                    timeReleased = Date().time
                    longPressHandler.removeCallbacks(_longPressed)
                    if(isLongPressLike) { //ดึงมือขึ้นจากการ hold
                        try {
                            heartPumpingSoundEffect!!.stop()
                        }catch (ex:Exception){

                        }
                        isLongPressLike = false
                        val r1 = Random()
                        var i1 = r1.nextInt(40 - 10 + 1) + 10
                        if((i1%2) == 0){
                            i1 = i1 * (-1)
                        }
                        val r2 = Random()
                        val i2 = r2.nextInt(120 - 60 + 1) + 60
                        val anim = TranslateAnimation(0f, -1f * i1, 0.0f, -1f * i2 )
                        anim.setDuration(500)
                        heartPumpingImageView!!.startAnimation(anim)

                        val fadeOut = ObjectAnimator.ofFloat(heartPumpingImageView, "alpha", 1f, .0f)
                        fadeOut.duration = 500
                        heartPumpingAnimationSet = AnimatorSet()
                        heartPumpingAnimationSet.play(fadeOut)//.after(fadeOut)
                        heartPumpingAnimationSet.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                heartPumpingImageView!!.setImageResource(0);
                                heartPumpingImageView!!.setImageDrawable(null);
                                heartPumpingImageView!!.setBackgroundDrawable(null)
                                var likevalue = disapearAnimateWhenTouchOutFromHold(timeClicked, timeReleased)
                            }
                        })
                        heartPumpingAnimationSet.start()
                    }else{


//                        this@IDLikeLayout.heartSoundEffect!!.seekTo(0)
//                        this@IDLikeLayout.heartSoundEffect!!.start()

                        isLongPressLike = false
                        val clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime
                        if (clickDuration < MAX_CLICK_DURATION) {
                            val pointerCount = event.getPointerCount()
                            val pointerId = event.getPointerId(0)
                        }
                        val pressDuration = System.currentTimeMillis() - pressStartTime
                        if (pressDuration < MAX_CLICK_DURATION && distance(pressedX, pressedY, event.getX(), event.getY()) < MAX_CLICK_DISTANCE) {
                            playBeep()
//                            this@IDLikeLayout.heartSoundEffect = MediaPlayer.create(context, R.raw.heartclick)
                            val imageView = ImageView(context)
                            prepareHeartClickImages(imageView)
                            imageView.scaleType = ImageView.ScaleType.FIT_XY
                            val layoutParams = LinearLayout.LayoutParams(140, 140)
                            imageView.layoutParams = layoutParams
                            val params = RelativeLayout.LayoutParams(140, 140)
                            params.leftMargin = event.x.toInt() - 70
                            params.topMargin = event.y.toInt() - 70
                            this@IDLikeLayout.addView(imageView, params)
                            val fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, .0f)
                            fadeOut.duration = 2000
                            val mAnimationSet = AnimatorSet()

                            heartClickAnimationDrawable.start()
                            mAnimationSet.play(fadeOut)//.after(fadeOut)

                            mAnimationSet.addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
//                                mAnimationSet.start()
                                }
                            })
                            mAnimationSet.start()

                        }
                    }
                }
                return false
            }

        })

    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x1 - x2
        val dy = y1 - y2
        val distanceInPx = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        return pxToDp(distanceInPx)
    }

    private fun pxToDp(px: Float): Float {
        return px / resources.displayMetrics.density
    }

    fun disapearAnimateWhenTouchOutFromHold(timeClicked: Long, timeReleased: Long): Int{
        var like = (((timeReleased - timeClicked)/1000.0)* 20.0).toInt()
        if(like > 100){
            like = 100
        }
        return like
    }

    fun playBeep() {
        var heartSoundEffect = MediaPlayer()
        try {
            var descriptor = context.getAssets().openFd("heartclick.mp3")
            heartSoundEffect.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength())
            descriptor.close()
            heartSoundEffect.prepare()

            heartSoundEffect.setVolume(1f, 1f)
            heartSoundEffect.setLooping(false)
            heartSoundEffect.seekTo(0)
            heartSoundEffect.setOnCompletionListener {
                heartSoundEffect.stop()
                heartSoundEffect.release()
            }
            heartSoundEffect.start()
        } catch (e:Exception) {
            try {
                heartSoundEffect.stop()
                heartSoundEffect.release()
            }catch (e:Exception) {
                heartSoundEffect.release()
            }
            e.printStackTrace()
        }
    }

    fun playPumping() {
        try {
            if (heartPumpingSoundEffect != null) {
                try {
//                    if (heartPumpingSoundEffect!!.isPlaying()) {
                        heartPumpingSoundEffect!!.stop()
                        heartPumpingSoundEffect!!.release()
//                    }
                }catch (e:Exception) {
                    heartPumpingSoundEffect!!.release()
                }
            }
            heartPumpingSoundEffect = MediaPlayer()
            var descriptor = context.getAssets().openFd("heartsound.mp3")
            heartPumpingSoundEffect!!.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength())
            descriptor.close()
            heartPumpingSoundEffect!!.prepare()

            heartPumpingSoundEffect!!.setVolume(1f, 1f)
            heartPumpingSoundEffect!!.setLooping(false)
            heartPumpingSoundEffect!!.seekTo(1000)
            heartPumpingSoundEffect!!.setOnCompletionListener {
//                heartPumpingSoundEffect!!.stop()
                heartPumpingSoundEffect!!.release()
            }
            heartPumpingSoundEffect!!.start()
        } catch (e:Exception) {
            try {
                heartPumpingSoundEffect!!.stop()
                heartPumpingSoundEffect!!.release()
            }catch (e:Exception) {
                heartPumpingSoundEffect!!.release()
            }
            e.printStackTrace()
        }
    }
}