package org.techdown.draganddraw

import android.graphics.PointF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class Box(val start :PointF) : Parcelable {
    var end:PointF=start

    val left:Float
    get()=Math.min(start.x,end.x)

    val right:Float
    get()=Math.max(start.x,end.x)

    val top:Float
    get()=Math.min(start.y,end.y)

    val bottom:Float
    get()=Math.max(start.y,end.y)
}