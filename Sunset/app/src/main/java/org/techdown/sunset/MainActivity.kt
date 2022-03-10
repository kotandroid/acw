package org.techdown.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var sceneView:View
    private lateinit var sunView:View
    private lateinit var skyView:View

    private val blueSkyColor :Int by lazy{
        ContextCompat.getColor(this,R.color.blue_sky)
    }
    private val sunsetSkyColor:Int by lazy{
        ContextCompat.getColor(this,R.color.sunset_sky)
    }
    private val nightSkyColor:Int by lazy{
        ContextCompat.getColor(this,R.color.night_sky)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sceneView=findViewById(R.id.scene)
        sunView=findViewById(R.id.sun)
        skyView=findViewById(R.id.sky)


        sceneView.setOnClickListener{
            startAnimation()
        }


    }
    private fun startAnimation(){
        val sunYStart=sunView.top.toFloat()
        val sunYEnd=skyView.height.toFloat()

        val heightAnimator=ObjectAnimator
            .ofFloat(sunView,"y",sunYStart,sunYEnd)
            .setDuration(3000)
        heightAnimator.interpolator=AccelerateInterpolator()

        val sunsetSkyAnimator=ObjectAnimator
            .ofInt(skyView,"backgroundColor",blueSkyColor,sunsetSkyColor)
            .setDuration(3000)
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightSkyAnimator=ObjectAnimator
            .ofInt(skyView,"backgroundColor",sunsetSkyColor,nightSkyColor)
            .setDuration(1500)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())


        val heightAnimator2=ObjectAnimator
            .ofFloat(sunView,"y",sunYEnd,sunYStart)
            .setDuration(3000)
        heightAnimator.interpolator=AccelerateInterpolator()

        val sunsetSkyAnimator2=ObjectAnimator
            .ofInt(skyView,"backgroundColor",sunsetSkyColor,blueSkyColor)
            .setDuration(3000)
        sunsetSkyAnimator2.setEvaluator(ArgbEvaluator())

        val nightSkyAnimator2=ObjectAnimator
            .ofInt(skyView,"backgroundColor",nightSkyColor,sunsetSkyColor)
            .setDuration(1500)
        nightSkyAnimator2.setEvaluator(ArgbEvaluator())

        val animator2_sub=AnimatorSet()
        animator2_sub.play(heightAnimator2)
            .with(sunsetSkyAnimator2)

        val animatorSet2= AnimatorSet()
        animatorSet2.play(nightSkyAnimator2)
            .before(animator2_sub)




        val animatorSet= AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            .before(nightSkyAnimator)
            .before(animatorSet2)


        animatorSet.start()



    }
}