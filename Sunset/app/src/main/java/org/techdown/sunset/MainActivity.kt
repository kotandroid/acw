package org.techdown.sunset

import android.animation.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var sceneView:View
    private lateinit var sunView:View
    private lateinit var skyView:View
    private lateinit var animatorSet: AnimatorSet
    private var sunflag=true

    private val blueSkyColor :Int by lazy{
        ContextCompat.getColor(this,R.color.blue_sky)
    }
    private val sunsetSkyColor:Int by lazy{
        ContextCompat.getColor(this,R.color.sunset_sky)
    }
    private val nightSkyColor:Int by lazy{
        ContextCompat.getColor(this,R.color.night_sky)
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sceneView=findViewById(R.id.scene)
        sunView=findViewById(R.id.sun)
        skyView=findViewById(R.id.sky)

        animatorSet= AnimatorSet()



        sceneView.setOnClickListener{
            startAnimation()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAnimation(){

        val sunYStart=sunView.top.toFloat()
        val sunYEnd=skyView.height.toFloat()

        val brightAnimator=ObjectAnimator
            .ofInt(sunView,"backgroundColor",blueSkyColor,sunsetSkyColor)
            .setDuration(500)
        brightAnimator.setEvaluator(ArgbEvaluator())
        brightAnimator.repeatCount=ValueAnimator.INFINITE

        val heightAnimator=
            ObjectAnimator
                .ofFloat(sunView,"y",sunYStart,sunYEnd)
                .setDuration(1500)


        val sunsetSkyAnimator=ObjectAnimator
            .ofInt(skyView,"backgroundColor",blueSkyColor,sunsetSkyColor)
            .setDuration(1500)

        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightSkyAnimator=ObjectAnimator
            .ofInt(skyView,"backgroundColor",sunsetSkyColor,nightSkyColor)
            .setDuration(700)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())



        animatorSet
            .play(heightAnimator).with(sunsetSkyAnimator)
            .before(nightSkyAnimator)


        val currentTime=animatorSet.currentPlayTime
            if(sunflag) {
                //해가 떠있을 때 , 해가 뜨고있을 때 터치
                Log.d("main","3    "+animatorSet.currentPlayTime+"         "+animatorSet.totalDuration)

                animatorSet.start()
                animatorSet.currentPlayTime=if(currentTime==0.toLong()) 0 else animatorSet.totalDuration-animatorSet.currentPlayTime

                sunflag=false
            }else{
                //해가 져있을 때, 해가 지고 있을 때 터치

                    Log.d("main","4       "+animatorSet.currentPlayTime)

                animatorSet.reverse()
                animatorSet.currentPlayTime=if(currentTime==0.toLong()) 0 else animatorSet.totalDuration-animatorSet.currentPlayTime

                sunflag=true

            }




    }
}