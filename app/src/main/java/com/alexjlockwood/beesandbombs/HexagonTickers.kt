package com.alexjlockwood.beesandbombs

//import androidx.compose.foundation.Canvas
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.withTransform
//import kotlin.math.abs
//import kotlin.math.sqrt
//
//private const val N = 10
//private const val sp = 36f
//private const val maxDist = 249.5f
//private const val easing = 4.5f
//private const val wavelength = 2.5f
//private const val numFrames = 330
//private val mn = sqrt(3f) / 2f
//
//@Composable
//fun HexagonTickers(modifier: Modifier = Modifier) {
//    val state = animationTimeMillis()
//
//    Canvas(modifier = modifier) {
//        withTransform({
//            scale(2.5f)
//            translate(size.width / 2f, size.height / 2f)
//        }, {
//            val t = (state.value / (20f * numFrames)) % 1f
//            //println("===== " + t)
//            for (a in -1..1) {
//                for (i in -N until N) {
//                    for (j in -N until N) {
//                        var x = i * sp
//                        val y = (j + a * 2 / 3.0f) * mn * sp
//                        if (j % 2 != 0) {
//                            x += .5f * sp
//                        }
//                        val hexDist =
//                            max(abs(y), abs(mn * x + .5f * y), abs(mn * x - .5f * y)) / maxDist
//                        if (hexDist <= 1) {
//                            val w = lerp(3f, 4f, sq(hexDist))
//                            val h = lerp(sp / mn, w, sq(hexDist))
//                            val tt = (t + 100 - hexDist / wavelength) % 1
//                            var th = TWO_PI * a / 3
//                            for (k in 0..2) {
//                                th += TWO_PI / 6 * ease(constrain(3 * tt - k, 0f, 1f), easing)
//                            }
//                            withTransform({
//                                translate(x, y)
//                                rotate(toDegrees(th), 0f, 0f)
//                            }) {
//                                drawOval(
//                                    color = Color.Black,
//                                    size = Size(w, h),
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        })
//    }
//}
