package com.example.testtelephoto.ui.screens

import androidx.compose.ui.geometry.Rect
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.testtelephoto.R
import kotlinx.coroutines.launch
import me.saket.telephoto.ExperimentalTelephotoApi
import me.saket.telephoto.zoomable.ZoomableContent
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.spatial.CoordinateSpace
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTelephotoApi::class)
@Composable
fun DrawingScreen() {
    val topBarColor = MaterialTheme.colorScheme.surface
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(topBarColor)
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
        val coroutineScope = rememberCoroutineScope()
        val onPan: (delta: DpOffset) -> Unit = { delta ->
            coroutineScope.launch {
                zoomableState.panBy(
                    offset = with(curDensity) {
                        Offset(x = delta.x.toPx(), y = delta.y.toPx())
                    },
                    animationSpec = SnapSpec(),
                )
            }
        }
        var clickPoint by remember { mutableStateOf(Offset.Zero) }
        CompositionLocalProvider(LocalViewConfiguration provides customViewConfiguration) {
            Box(modifier = Modifier.fillMaxSize()) {
                ZoomableAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.ex)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    state = imageState,
                    onDoubleClick = { _, _ -> },
                    onClick = { offset ->
                        clickPoint = offset
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
                    val transformation = zoomableState.contentTransformation
                    val scale = transformation.scale
                    val offsetX = transformation.offset.x
                    val offsetY = transformation.offset.y
                    val visibleImageBounds = with(imageState.zoomableState.coordinateSystem) {
                        contentBounds.rectIn(CoordinateSpace.ZoomableContent)
                    }
                    val topLeft = Offset(
                        x = visibleImageBounds.left * scale.scaleX + offsetX,
                        y = visibleImageBounds.top * scale.scaleY + offsetY
                    )
                    val bottomRight = Offset(
                        x = visibleImageBounds.right * scale.scaleX + offsetX,
                        y = visibleImageBounds.bottom * scale.scaleY + offsetY
                    )
                    val newVisibleImageBounds = Rect(topLeft, bottomRight)


                    val checkOnClick = true

                    if (checkOnClick) {
                        if (clickPoint != Offset.Zero) {
                            DrawHandle(
                                baseOffset = clickPoint,
                                onDrag = {

                                },
                                curDensity = curDensity,
                                pointMovingSizeDp = 100.dp * scale.scaleX,
                                strokeMovingDp = 5.dp * scale.scaleX,
                                visibleImageBounds = newVisibleImageBounds,
                                onPan = onPan,
                                onEnd = {

                                }
                            )
                        }
                    } else {
                        var randomCords = Offset(3000f, 3000f)
                        val frX = randomCords.x * scale.scaleX + offsetX
                        val frY = randomCords.y * scale.scaleY + offsetY
                        val baseOffset = Offset(frX, frY)
                        DrawHandle(
                            baseOffset = baseOffset,
                            onDrag = {

                            },
                            curDensity = curDensity,
                            pointMovingSizeDp = 100.dp * scale.scaleX,
                            strokeMovingDp = 5.dp * scale.scaleX,
                            visibleImageBounds = newVisibleImageBounds,
                            onPan = onPan,
                            onEnd = {

                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawHandle(
    baseOffset: Offset,
    onDrag: (Offset) -> Unit,
    pointMovingSizeDp: Dp,
    strokeMovingDp: Dp,
    curDensity: Density,
    onPan: (DpOffset) -> Unit,
    visibleImageBounds: Rect,
    onEnd: () -> Unit,
) {
    Box(
        modifier = pointModifierWithSwipe(
            x = baseOffset.x,
            y = baseOffset.y,
            size = pointMovingSizeDp,
            onDragEnd = { local ->
                onEnd
            },
            onDrag = { dragAmount -> onDrag(dragAmount) },
            borderWidth = strokeMovingDp,
            curDensity = curDensity,
            onPan = onPan,
            visibleImageBounds = visibleImageBounds
        )
    )
}

@Composable
fun pointModifierWithSwipe(
    x: Float,
    y: Float,
    size: Dp,
    onDragEnd: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    borderWidth: Dp,
    visibleImageBounds: Rect,
    onPan: (DpOffset) -> Unit,
    curDensity: Density,
): Modifier {
    val pickedColor = Color.Red
    var isDragging by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        targetValue = if (isDragging) Color.Yellow.copy(alpha = 0.7f) else Color.Transparent,
        animationSpec = tween(durationMillis = 150), label = ""
    )
    var localOffset by remember(x, y) { mutableStateOf(Offset(x, y)) }
    val edgeThresholdPx = with(curDensity) { 48.dp.toPx() }
    val panSpeedDp = 12.dp
    val modifier = Modifier
        .offset {
            IntOffset(
                (localOffset.x - size.toPx() / 2).roundToInt(),
                (localOffset.y - size.toPx() / 2).roundToInt()
            )
        }
        .size(size)
        .background(
            color = animatedColor,
            shape = CircleShape
        )
        .border(
            width = borderWidth,
            color = pickedColor,
            shape = CircleShape
        )
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { isDragging = true },
                onDragEnd = {
                    isDragging = false
                    onDragEnd(localOffset)
                },
                onDragCancel = { isDragging = false },
                onDrag = { change, dragAmount ->
                    change.consume()
                    localOffset += dragAmount
                    onDrag(dragAmount)
                    var panX = 0.dp
                    var panY = 0.dp
                    if (localOffset.x < visibleImageBounds.left + edgeThresholdPx) {
                        panX = panSpeedDp
                    }
                    if (localOffset.x > visibleImageBounds.right - edgeThresholdPx) {
                        panX = -panSpeedDp
                    }
                    if (localOffset.y < visibleImageBounds.top + edgeThresholdPx) {
                        panY = panSpeedDp
                    }
                    if (localOffset.y > visibleImageBounds.bottom - edgeThresholdPx) {
                        panY = -panSpeedDp
                    }
                    if (panX != 0.dp || panY != 0.dp) {
                        onPan(DpOffset(panX, panY))
                    }
                }
            )
        }
    return modifier
}