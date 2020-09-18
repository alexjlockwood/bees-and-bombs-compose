package com.alexjlockwood.beesandbombs

//import androidx.compose.foundation.Canvas
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.State
//import androidx.compose.runtime.dispatch.withFrameMillis
//import androidx.compose.runtime.launchInComposition
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.drawscope.withTransform
//import androidx.compose.ui.platform.LifecycleOwnerAmbient
//import androidx.lifecycle.whenStarted
//import kotlin.math.*
//
//private const val N = 36
//private const val l = 40
//private val mn = sqrt(3f) / 2f
//private val ia = atan(sqrt(0.5f))
//private const val r0 = l / (TWO_PI / 3f)
//private const val sp = 3f * r0
//
//private val path = Path()
//
//@Composable
//fun ArcDance(modifier: Modifier = Modifier) {
//    val state = animationTimeMillis()
//
//    Canvas(modifier = modifier) {
//        withTransform({
//            translate(size.width / 2f, size.height / 2f)
//            //rotate(HALF_PI, pivotX = 0f, pivotY = 0f)
//        }, {
//            val value = state.value
//            val t = (value / (20f * 100)) % 1f
//            for (fr in 0..2) {
//                //stroke(cs.get(fr))
//                for (i in -7..7) {
//                    for (j in -9..9) {
//                        var x = (i * sp)
//                        var y = ((j + 2f / 3f) * mn * sp)
//                        if (j % 2 != 0) {
//                            x += (0.5f * sp).toInt()
//                        }
//                        var tt = map(
//                            cos(
//                                TWO_PI * t - 0.06f * fr + atan2(x, y) - 0.008f * dist(
//                                    x,
//                                    y,
//                                    0f,
//                                    0f
//                                )
//                            ), 1f, -1f, 0f, 1f
//                        )
//                        tt = ease(tt, 3f)
//                        withTransform({
//                            translate(x, y)
//                        }, {
//                            for (a in 0..2) {
//                                withTransform({
//                                    rotate(TWO_PI * a / 3, 0f, 0f)
//                                    translate(r0, 0f)
//                                }, {
//                                    val q = tt
//                                    val ex = lerp(
//                                        TWO_PI / 3,
//                                        1e-5f,
//                                        constrain(2 * q, 0f, 1f) - constrain(2 * q - 1, 0f, 1f)
//                                    );
//                                    val r = l / ex
//                                    withTransform({
//                                        scale(if (q >= 0.5f) -1f else 1f, 1f)
//                                    }, {
//                                        for (i in 0..N) {
//                                            val qq = i / (N - 1).toFloat();
//                                            val th = lerp(-ex / 2, ex / 2, qq);
//                                            x = r * cos(th) - r
//                                            y = r * sin(th)
//                                            path.lineTo(x, y)
//                                        }
//                                    })
//                                    path.close()
//                                    drawPath(
//                                        path = path,
//                                        color = Color.Black,
//                                        style = Stroke(width = 2f),
//                                    )
//                                    path.reset()
//                                })
//                            }
//                        })
//                    }
//                }
//            }
//        })
//    }
//}
