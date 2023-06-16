package github.leavesczy.matisse.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.PhotoCamera
//import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MediaResource
import github.leavesczy.matisse.R
import github.leavesczy.matisse.internal.logic.MatisseBottomBarViewState
import github.leavesczy.matisse.internal.logic.MatissePageViewState
import github.leavesczy.matisse.internal.logic.MatisseTopBarViewState
import github.leavesczy.matisse.internal.utils.isVideo

/**
 * @Author: CZY
 * @Date: 2022/5/31 16:36
 * @Desc:
 */
@Composable
internal fun MatissePage(
    matisse: Matisse,
    matissePageViewState: MatissePageViewState,
    matisseTopBarViewState: MatisseTopBarViewState,
    matisseBottomBarViewState: MatisseBottomBarViewState,
    selectedResources: List<MediaResource>,
    onRequestTakePicture: () -> Unit,
    onSure: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorResource(id = R.color.matisse_main_page_background_color),
        topBar = {
            MatisseTopBar(
                matisse = matisse,
                topBarViewState = matisseTopBarViewState
            )
        },
        bottomBar = {
            MatisseBottomBar(
                bottomBarViewState = matisseBottomBarViewState,
                onSure = onSure
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
            columns = GridCells.Fixed(count = integerResource(id = R.integer.matisse_image_span_count)),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            val selectedBucket = matissePageViewState.selectedBucket
            if (selectedBucket.supportCapture) {
                item(
                    key = "Capture",
                    contentType = "Capture",
                    content = {
                        CaptureItem(onClick = onRequestTakePicture)
                    }
                )
            }
            items(
                items = selectedBucket.resources,
                key = {
                    it.id
                },
                contentType = {
                    "Media"
                },
                itemContent = {
                    val mediaPlacement by remember(key1 = selectedResources) {
                        val index = selectedResources.indexOf(element = it)
                        val isSelected = index > -1
                        val enabled = isSelected || selectedResources.size < matisse.maxSelectable
                        val position = if (isSelected) {
                            (index + 1).toString()
                        } else {
                            ""
                        }
                        mutableStateOf(
                            value = MediaPlacement(
                                isSelected = isSelected,
                                enabled = enabled,
                                position = position
                            )
                        )
                    }
                    MediaItem(
                        matisse = matisse,
                        mediaResource = it,
                        mediaPlacement = mediaPlacement,
                        onClickMedia = matissePageViewState.onClickMedia,
                        onClickCheckBox = matissePageViewState.onMediaCheckChanged
                    )
                }
            )
        }
    }
}

@Stable
private data class MediaPlacement(
    val isSelected: Boolean,
    val enabled: Boolean,
    val position: String
)

@Composable
private fun LazyGridItemScope.MediaItem(
    matisse: Matisse,
    mediaResource: MediaResource,
    mediaPlacement: MediaPlacement,
    onClickMedia: (MediaResource) -> Unit,
    onClickCheckBox: (MediaResource) -> Unit
) {
    Box(
        modifier = Modifier
            .animateItemPlacement()
            .padding(all = 1.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = RoundedCornerShape(size = 4.dp))
            .background(color = colorResource(id = R.color.matisse_image_item_background_color))
            .then(
                other = if (mediaPlacement.isSelected) {
                    Modifier.drawBorder(color = colorResource(id = R.color.matisse_image_item_border_color_when_selected))
                } else {
                    Modifier
                }
            )
            .clickable {
                onClickMedia(mediaResource)
            }
    ) {
        matisse.imageEngine.Image(
            modifier = Modifier.fillMaxSize(),
            model = mediaResource.uri,
            contentScale = ContentScale.Crop,
            contentDescription = mediaResource.name
        )
        MatisseCheckbox(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(all = 3.dp),
            text = mediaPlacement.position,
            checked = mediaPlacement.isSelected,
            enabled = mediaPlacement.enabled,
            onClick = {
                onClickCheckBox(mediaResource)
            }
        )
        if (mediaResource.isVideo) {
            Icon(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .size(size = 32.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.slow_motion_video),//Icons.Filled.SlowMotionVideo,
                tint = colorResource(id = R.color.matisse_video_icon_color),
                contentDescription = mediaResource.name
            )
        }
    }
}

@Composable
private fun LazyGridItemScope.CaptureItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .animateItemPlacement()
            .padding(all = 1.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = RoundedCornerShape(size = 4.dp))
            .background(color = colorResource(id = R.color.matisse_image_item_background_color))
            .clickable(onClick = onClick)
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(fraction = 0.5f)
                .align(alignment = Alignment.Center),
            imageVector = ImageVector.vectorResource(id = R.drawable.photo_camera),//Icons.Filled.PhotoCamera,
            tint = colorResource(id = R.color.matisse_capture_icon_color),
            contentDescription = "Capture"
        )
    }
}

private fun Modifier.drawBorder(color: Color): Modifier {
    return drawWithCache {
        val lineWidth = 3.dp.toPx()
        val topLeftPoint = lineWidth / 2f
        val rectSize = size.width - lineWidth
        onDrawWithContent {
            drawContent()
            drawRect(
                color = color,
                topLeft = Offset(topLeftPoint, topLeftPoint),
                size = Size(width = rectSize, height = rectSize),
                style = Stroke(width = lineWidth)
            )
        }
    }
}