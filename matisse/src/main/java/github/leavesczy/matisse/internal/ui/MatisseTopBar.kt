package github.leavesczy.matisse.internal.ui

import android.app.Activity
import android.graphics.drawable.Icon
import android.inputmethodservice.Keyboard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.R
import github.leavesczy.matisse.internal.logic.MatisseTopBarViewState
import github.leavesczy.matisse.internal.utils.clickableNoRipple

/**
 * @Author: leavesCZY
 * @Date: 2021/6/24 16:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
internal fun MatisseTopBar(matisse: Matisse, topBarViewState: MatisseTopBarViewState) {
    Row(
        modifier = Modifier
            .shadow(elevation = 4.dp)
            .background(color = colorResource(id = R.color.matisse_status_bar_color))
            .statusBarsPadding()
            .fillMaxWidth()
            .height(height = 56.dp)
            .background(color = colorResource(id = R.color.matisse_top_bar_background_color)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var menuExpanded by remember {
            mutableStateOf(value = false)
        }
        Row(
            modifier = Modifier
                .padding(end = 30.dp)
                .clickableNoRipple {
                    menuExpanded = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            Icon(
                modifier = Modifier
                    .clickableNoRipple {
                        (context as Activity).finish()
                    }
                    .padding(start = 18.dp, end = 12.dp)
                    .fillMaxHeight()
                    .size(size = 22.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.arrow_back_ios),// Icons.Filled.ArrowBackIos,
                tint = colorResource(id = R.color.matisse_top_bar_icon_color),
                contentDescription = null
            )
            Text(
                modifier = Modifier.weight(weight = 1f, fill = false),
                text = topBarViewState.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontSize = 19.sp,
                color = colorResource(id = R.color.matisse_top_bar_text_color)
            )
            Icon(
                modifier = Modifier.size(size = 32.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.arrow_drop_down),//Icons.Filled.ArrowDropDown,
                tint = colorResource(id = R.color.matisse_top_bar_icon_color),
                contentDescription = null
            )
        }
        BucketDropdownMenu(
            matisse = matisse,
            topBarViewState = topBarViewState,
            menuExpanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            }
        )
    }
}

@Composable
private fun BucketDropdownMenu(
    matisse: Matisse,
    topBarViewState: MatisseTopBarViewState,
    menuExpanded: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier
            .background(color = colorResource(id = R.color.matisse_dropdown_menu_background_color))
            .widthIn(min = 220.dp)
            .heightIn(max = 400.dp),
        expanded = menuExpanded,
        offset = DpOffset(x = 10.dp, y = (-10).dp),
        onDismissRequest = onDismissRequest
    ) {
        for (bucket in topBarViewState.mediaBuckets) {
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                text = {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        matisse.imageEngine.Image(
                            modifier = Modifier
                                .size(size = 52.dp)
                                .clip(shape = RoundedCornerShape(size = 4.dp))
                                .background(color = colorResource(id = R.color.matisse_image_item_background_color)),
                            model = bucket.displayIcon,
                            contentScale = ContentScale.Crop,
                            contentDescription = bucket.displayName
                        )
                        Text(
                            modifier = Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(start = 10.dp),
                            text = bucket.displayName,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.matisse_dropdown_menu_text_color),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                        Text(
                            modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                            text = "(${bucket.resources.size})",
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.matisse_dropdown_menu_text_color),
                            maxLines = 1
                        )
                    }
                },
                onClick = {
                    onDismissRequest()
                    topBarViewState.onClickBucket(bucket)
                }
            )
        }
    }
}