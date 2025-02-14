package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ValentinesDay(modifier: Modifier = Modifier) {
    var restartAnimation by remember { mutableStateOf(false) }
    val animatedProgresses = remember { Animations.map { it to Animatable(0f) } }

    LaunchedEffect(restartAnimation) {
        for ((animation, animatable) in animatedProgresses) {
            animatable.snapTo(0f)
            launch {
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = animation.duration,
                        delayMillis = animation.startTime,
                        easing = LinearEasing,
                    ),
                )
            }
        }
    }

    Image(
        modifier = modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = { restartAnimation = !restartAnimation },
        ),
        painter = rememberVectorPainter(
            defaultWidth = 3038.dp,
            defaultHeight = 1551.dp,
            viewportWidth = 3038f,
            viewportHeight = 1551f,
            autoMirror = false,
        ) { vw, vh ->
            Path(
                pathData = remember { addPathNodes("h $vw v $vh h -$vw v -$vh") },
                fill = SolidColor(Color.White),
            )
            animatedProgresses.forEach { (info, animatable) ->
                Path(
                    pathData = info.pathData,
                    stroke = SolidColor(StrokeColor),
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineWidth = 35f,
                    trimPathEnd = animatable.value,
                )
            }
        },
        contentDescription = null,
    )
}

// Drawn in Figma as a single stroked VectorNode and exported as an SVG. Then imported
// the SVG into https://shapeshifter.design to re-order the incorrect paths/subpaths
// in the SVG string. Then re-exported and split up into individual subpaths so
// we can animate the trimOffsetEnd of each sequentially.
//
// Format: `startTime to listOf sequential animations`, where each sequential animation
// is represented by `durationMillis to stroked-subpath to animate`.
//
// It is stored this way primarily just so I could easily tweak the animations without going
// sane, since some stroked subpaths animate in parallel with different timings.
private val Animations = listOf(
    0.00f to listOf(0.15f to "M 1074 21 C 1022.92 106.782 981.854 183.96 953.454 250.459 C 906.44 360.545 894.128 441.363 928.499 483.5"),
    0.10f to listOf(0.10f to "M 840 255.5 C 882.872 253.996 919.775 252.396 953.454 250.459 C 1038.3 245.578 1102.68 238.552 1190.5 225.5"),
    0.15f to listOf(0.10f to "M 1310 43 C 1240.53 138.805 1207.39 192.892 1166 290.5 C 1148.78 358.39 1161.4 376.346 1225 373.5"),
    0.22f to listOf(
        0.14f to "M 1470.5 196 C 1465.49 186.765 1440.09 154.393 1400.5 172.5 C 1360.91 190.607 1227.16 316.309 1267 372 C 1306.84 427.691 1433.68 231.301 1439.5 223.756 C 1445.32 216.212 1369.62 383.767 1446 396.5",
        0.16f to "M 1517 157.5 C 1544.71 152.835 1565.52 145.99 1582.5 152 C 1599.48 158.01 1557.62 241.778 1541.5 275 C 1498.06 372.429 1502.96 367.601 1484.5 413 C 1469.8 451.299 1465.67 472.764 1456 511.5 C 1450.2 540.301 1448.3 551.842 1465 566.5 C 1480 570 1489.94 567.76 1509 554.5",
    ),
    0.42f to listOf(
        0.10f to "M 1558 247.5 C 1586.86 218.112 1623.5 178 1667 178 C 1698.5 178 1702.5 190 1703 216.5 C 1703.5 243 1685 284.5 1669.5 307 C 1621.18 364.842 1588.03 380.518 1517.5 377",
        0.40f to "M 1753.5 158 C 1753.5 158 1769.53 150.216 1797.5 152 C 1825.47 153.783 1799 221 1799 221 L 1688.31 483 L 1669.38 522 C 1669.38 522 1630.35 603.764 1622.5 616 C 1597.5 662.677 1579.97 684.609 1536.5 709 C 1507.37 726.923 1479.5 734 1457.5 730 C 1435.5 726 1406.5 703 1394.5 659 C 1386.95 608.98 1394.5 572.5 1429.5 537.5 C 1469.5 504.5 1518.68 490.081 1566 492.5 C 1594.55 492.855 1617.06 493.831 1636 495.686 C 1639.09 495.988 1649 497 1656 498 C 1663 499 1699.5 508.735 1699.5 508.735 C 1699.5 508.735 1720.5 515 1736.5 524 C 1752.5 533 1796.88 562.357 1831 619.5 C 1856.5 672 1858 730 1850.5 761 C 1843 792 1826.65 824.345 1800.5 849 C 1750.82 883.226 1701.5 892 1649.5 873 C 1597.5 854 1582.02 840.006 1572.5 822 C 1562.98 803.994 1553.22 779.97 1570 757 C 1586.78 734.03 1608 746.5 1618.5 755 C 1629 763.5 1627.5 774 1636 774 C 1644.5 774 1648.5 740.5 1666.5 731.5 C 1684.5 722.5 1708.5 735 1715.5 759.5 C 1722.5 784 1688.5 840 1688.5 840",
        0.42f to "M 2011 161 C 1994.12 261.607 1906 421.5 1981 425 C 2056 428.5 2155.87 157.897 2163.5 168 C 2171.13 178.103 2154.5 337 2106.5 432.5 C 2058.5 528 1984.5 609 1928.5 646.5 C 1872.5 684 1771.52 706.437 1588.5 707 C 1503.28 704.97 1372.3 692.415 1312.5 662 C 1252.7 631.586 1216.23 591.402 1208.5 534 C 1200.77 476.598 1237 461 1251 458 C 1265 455 1283.5 513.5 1288.5 511.5 C 1293.5 509.5 1311 446 1340 455 C 1369 464 1372.86 531.607 1326.5 606",
        0.20f to "M 178.5 1040 C 211.918 1032.62 242.5 1040 251 1061.5 C 259.5 1083 247.5 1115.5 230 1143.5 C 212.5 1171.5 199 1187 167.5 1207 C 136 1227 107.5 1235 78.5 1221.5 C 49.5 1208 22.5 1172 21 1096.5 C 19.5 1021 83.343 939.222 123.5 907 C 168.473 865.829 266.501 820.056 337.5 847.5 C 401.36 872.185 419.87 931.503 396.5 1016 C 365.168 1088.95 341.506 1127.22 299 1213 C 269.97 1269.53 250 1331 295.5 1353 C 341 1375 437.514 1289.03 498.5 1207 C 582.5 1087 599.064 1006.87 512.5 959",
        0.16f to "M 766.5 1124.5 C 766.5 1124.5 754.307 1113.98 719.002 1110.5 C 683.698 1107.02 634 1147 607 1198 C 580 1249 545.505 1300.11 590.502 1324 C 635.5 1347.89 731.497 1173.78 741.502 1164 C 751.507 1154.22 722.606 1195.54 709.5 1231.5 C 698.952 1260.45 696.671 1281.48 698.501 1291 C 700.33 1300.52 739.832 1282.19 741.502 1279",
        0.15f to "M 911.5 904 C 853.364 1001.92 828.866 1058.16 799.5 1161 C 778.214 1276.29 799.417 1317.2 883.5 1357",
        0.15f to "M 920.5 1191 C 961.06 1204.02 988 1202 1027.5 1182 C 1067 1162 1078.88 1113.31 1061.5 1092.5 C 1044.12 1071.69 1007.5 1073 979 1104 C 950.5 1135 909.478 1182.84 897 1274.5 C 884.521 1366.16 1024.4 1282.53 1048.5 1244",
        0.16f to "M 1141.5 1094 C 1112.99 1175.51 1073.59 1291.9 1079.5 1297 C 1085.41 1302.1 1181.29 1116.59 1219.5 1116 C 1257.71 1115.41 1215.57 1175.34 1213.5 1229 C 1202.63 1282.43 1220.38 1322.16 1236.5 1322",
        0.16f to "M 1419.5 844 C 1408.87 848.346 1319.07 1044.97 1282.5 1171 C 1245.93 1297.03 1258.28 1422.7 1366 1423",
        0.10f to "M 1302.5 979 C 1392.14 923.201 1430.77 915.752 1473.5 956",
        0.13f to "M 1436.5 1096 C 1405.15 1169.38 1379.78 1248.23 1377.5 1287.5 C 1375.22 1326.77 1436.5 1277.5 1436.5 1277.5",
        0.10f to "M 1436.5 1020.5 C 1436.5 1005 1441 1005 1454.5 1005 C 1468 1005 1473.55 1005.67 1473.5 1022 C 1473.45 1038.33 1467.46 1038.97 1457 1039.5 C 1446.54 1040.03 1436.5 1036 1436.5 1020.5",
        0.17f to "M 1523.5 1097 C 1496.22 1184.57 1455.88 1289.5 1461.5 1289 C 1467.12 1288.5 1549.6 1114.08 1596.5 1125 C 1643.41 1135.92 1532.75 1293.04 1604.5 1324",
        0.16f to "M 1681.5 1193.5 C 1724.94 1201.71 1735 1205.5 1779.5 1186 C 1824 1166.5 1857 1104.5 1807.5 1081 C 1758 1057.5 1704.5 1135 1685.5 1170 C 1666.5 1205 1624 1281.1 1670.5 1311 C 1717 1340.9 1814.5 1231 1814.5 1231",
        0.09f to "M 1889.5 901 C 1919.88 938.14 1916.67 959.047 1874.5 1003",
        0.20f to "M 2090 1121 C 2090 1121 2093.23 1081.89 2039.5 1092.5 C 1985.77 1103.11 1941 1159 1968.5 1195 C 1996 1231 2043 1203 2061.5 1235.5 C 2080 1268 2059.41 1286.11 2034.5 1302 C 2009.59 1317.89 1968.5 1319 1953 1302 C 1937.5 1285 1937 1255.5 1937 1255.5",
        0.30f to "M 2330.5 1005 C 2322.7 975.806 2325 946 2341 927 C 2357 908 2377.5 911.5 2392.5 917 C 2407.5 922.5 2398 973 2403.5 975 C 2409 977 2439.5 955 2453.5 967 C 2467.5 979 2449 1021 2403.5 1046 C 2358 1071 2297.1 1056.85 2255.5 1020.5 C 2213.9 984.153 2227.38 938.386 2243 912 C 2258.62 885.614 2280.5 865 2326 848.5 C 2371.5 832 2444.48 847.632 2498 884.5 C 2551.52 921.368 2578.97 979.317 2589 1046 C 2599.03 1112.68 2598.66 1203.56 2556.5 1289 C 2514.34 1374.44 2425.81 1410.83 2296 1399 C 2319.167 1290.667 2342.333 1182.333 2365.5 1074",
        0.15f to "M 2839.5 1137 C 2839.5 1137 2815.5 1105 2776.5 1106 C 2737.5 1107 2671.74 1200.58 2660 1220 C 2648.26 1239.42 2611.18 1293.5 2648.09 1318 C 2658.58 1324.96 2676.4 1322.83 2694 1309 C 2732.6 1270.5 2799 1162.5 2802 1165 C 2805 1167.5 2743.92 1278.71 2755.5 1290 C 2767.08 1301.29 2805 1276 2805 1276",
        0.30f to "M 2900.5 1108 C 2860.03 1181.49 2843.18 1272.33 2864.5 1286 C 2885.82 1299.67 2951.48 1214.16 2955.5 1207 C 2963.92 1192 2996.4 1107.62 3014.5 1108 C 3037.55 1109.14 2931.78 1359.59 2917 1387 C 2902.22 1414.41 2871.13 1467.31 2794 1503.5 C 2716.87 1539.69 2648.09 1536.5 2596.5 1513 C 2544.91 1489.5 2533 1465.5 2530.5 1434 C 2528 1402.5 2545.5 1374 2567.5 1374 C 2589.5 1374 2589.5 1407 2602.5 1407 C 2615.5 1407 2622.43 1365.91 2643.5 1369 C 2664.57 1372.09 2665 1407 2657 1425.5 C 2649 1444 2623.5 1477 2623.5 1477",
    ),
    0.6f to listOf(0.18f to "M 1790.5 253.5 C 1829.99 215.789 1885 172 1912.5 176.5 C 1940 181 1949 202.5 1947.5 228 C 1946 253.5 1921.67 294.052 1897.5 327 C 1841.5 377.507 1806.48 383.598 1740.5 373"),
).flatMap { (startTime, animations) ->
    val flattenedAnimations = mutableListOf<AnimationInfo>()
    var currentStartTime = startTime
    animations.forEachIndexed { index, (duration, pathData) ->
        flattenedAnimations.add(
            AnimationInfo(
                startTime = (currentStartTime * 1000 * DurationScale).toInt(),
                duration = (duration * 1000 * DurationScale).toInt(),
                pathData = addPathNodes(pathData),
            )
        )
        currentStartTime += duration
    }
    flattenedAnimations
}

private data class AnimationInfo(
    val startTime: Int,
    val duration: Int,
    val pathData: List<PathNode>,
)

// Just an easy way for me to slows down the animation a tiny bit.
private const val DurationScale = 1.5f

private val StrokeColor = Color(0xFFE42600)