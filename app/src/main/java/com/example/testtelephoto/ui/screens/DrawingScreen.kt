package com.example.testtelephoto.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.testtelephoto.R
import me.saket.telephoto.zoomable.ZoomableContentTransformation
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import kotlin.math.ceil
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen() {
    val topBarColor = MaterialTheme.colorScheme.surface
    val isDark = isSystemInDarkTheme()
    val colorBar = if (isDark) Color.White else Color.Black
    val numOfLines = 200
    val listOfCords = mutableListOf<Pair<Coordinates, Coordinates>>()
    repeat(numOfLines) {
        listOfCords.add(
            Pair(
                Coordinates(
                    Random.nextInt(1000, 13000).toFloat(),
                    Random.nextInt(1000, 10001).toFloat(),
                ),
                Coordinates(
                    Random.nextInt(1000, 13000).toFloat(),
                    Random.nextInt(1000, 10001).toFloat(),
                )
            )
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {},
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorBar
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = colorBar,
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(topBarColor)
                .padding(paddingValues)
        ) {
            val zoomSpec = ZoomSpec(maxZoomFactor = 3f)
            val zoomableState = rememberZoomableState(zoomSpec = zoomSpec)
            val customViewConfiguration =
                object : ViewConfiguration by LocalViewConfiguration.current {
                    override val doubleTapTimeoutMillis: Long
                        get() = 0
                }
            val imageState = rememberZoomableImageState(zoomableState)
            var containerSizeDp: Pair<Dp, Dp> by remember { mutableStateOf(Pair(0.dp, 0.dp)) }
            val curDensity = LocalDensity.current
            var containerSizePx by remember { mutableStateOf(IntSize.Zero) }
            CompositionLocalProvider(LocalViewConfiguration provides customViewConfiguration) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ZoomableAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.ex)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build(),
                        state = imageState,
                        onDoubleClick = { _, _ -> },
                        onClick = {

                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                if (coordinates.size != containerSizePx) {
                                    containerSizePx = coordinates.size
                                    val widthDp = with(curDensity) { coordinates.size.width.toDp() }
                                    val heightDp =
                                        with(curDensity) { coordinates.size.height.toDp() }
                                    containerSizeDp = Pair(widthDp, heightDp)
                                }
                            },
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                    if (containerSizeDp != Pair(
                            0.dp,
                            0.dp
                        ) && containerSizePx.height != 0 && containerSizePx.width != 0
                    ) {
                        val strokeWidthForLine = 14f
                        val transformation = zoomableState.contentTransformation
                        for (line in listOfCords) {
                            val frX = line.first.x
                            val frY = line.first.y
                            val scX = line.second.x
                            val scY = line.second.y
                            createLine(
                                appCoordinatesFirst = Coordinates(frX, frY),
                                appCoordinatesLast = Coordinates(scX, scY),
                                containerSizePx = containerSizePx,
                                containerSizeDp = containerSizeDp,
                                transformation = transformation,
                                strokeWidthForLine = strokeWidthForLine,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun createLine(
    appCoordinatesFirst: Coordinates,
    appCoordinatesLast: Coordinates,
    containerSizePx: IntSize,
    containerSizeDp: Pair<Dp, Dp>,
    transformation: ZoomableContentTransformation,
    strokeWidthForLine: Float,
    color: Color,
) {
    val scale = transformation.scale
    val offsetX = transformation.offset.x
    val offsetY = transformation.offset.y
    val frX = (appCoordinatesFirst.x)
    val frY = (appCoordinatesFirst.y)
    val scX = (appCoordinatesLast.x)
    val scY = (appCoordinatesLast.y)
    val (minX, maxX) = if (frX < scX) frX to scX else scX to frX
    val (minY, maxY) = if (frY < scY) frY to scY else scY to frY
    val width = maxX - minX
    val height = maxY - minY
    val widthDp = with(LocalDensity.current) { width.toDp() }
    val heightDp = with(LocalDensity.current) { height.toDp() }
    val necessaryWidth = if (widthDp <= 0.dp) 30.dp else widthDp
    val necessaryHeight = if (heightDp <= 0.dp) 30.dp else heightDp
    val maxBoxWidth = containerSizeDp.first
    val maxBoxHeight = containerSizeDp.second
    val numBoxesX = ceil(necessaryWidth / maxBoxWidth).toInt()
    val numBoxesY = ceil(necessaryHeight / maxBoxHeight).toInt()
    for (boxX in 0 until numBoxesX) {
        for (boxY in 0 until numBoxesY) {
            val currentMinX = minX + boxX * containerSizePx.width
            val currentMinY = minY + boxY * containerSizePx.height
            val currentWidth =
                min(maxBoxWidth, necessaryWidth - boxX * maxBoxWidth)
            val currentHeight =
                min(maxBoxHeight, necessaryHeight - boxY * maxBoxHeight)
            Box(
                modifier = defectModifier(
                    scale = scale,
                    minX = currentMinX,
                    minY = currentMinY,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    width = currentWidth,
                    height = currentHeight
                )
            )
        }
    }
    Canvas(modifier = Modifier) {
        val startX =
            (frX * scale.scaleX) + offsetX
        val startY =
            (frY * scale.scaleY) + offsetY
        val endX =
            (scX * scale.scaleX) + offsetX
        val endY =
            (scY * scale.scaleY) + offsetY
        drawLine(
            color = color,
            start = Offset(
                startX,
                startY
            ),
            end = Offset(
                endX,
                endY
            ),
            strokeWidth = strokeWidthForLine * scale.scaleX
        )
    }
}

data class Coordinates(
    val x: Float,
    val y: Float,
)

@Composable
fun defectModifier(
    scale: ScaleFactor,
    minX: Float,
    minY: Float,
    offsetX: Float,
    offsetY: Float,
    width: Dp,
    height: Dp,
): Modifier {
    val modifier = Modifier
        .graphicsLayer {
            scaleX = scale.scaleX
            scaleY = scale.scaleY
            translationX =
                (minX * scale.scaleX) + offsetX
            translationY =
                (minY * scale.scaleY) + offsetY
            transformOrigin = TransformOrigin(0f, 0f)
        }
        .size(
            width = width,
            height = height
        )

    return modifier
}