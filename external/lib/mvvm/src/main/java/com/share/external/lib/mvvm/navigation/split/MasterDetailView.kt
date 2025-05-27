package com.share.external.lib.mvvm.navigation.split

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MasterDetailView(
    splitView: Boolean,
    modifier: Modifier = Modifier,
    fraction: Float = 0.25f,
    divider: Boolean = false,
    inverse: Boolean = false,
    initialShowDetail: Boolean = false,
    leadBackground: Color = Color.Transparent,
    trailingBackground: Color = Color.Transparent,
    master: @Composable BoxScope.(MutableState<Boolean>) -> Unit,
    detail: @Composable BoxScope.(MutableState<Boolean>) -> Unit,
) {
    val showDetail = rememberSaveable { mutableStateOf(initialShowDetail) }
    MasterDetailView(
        splitView,
        modifier,
        fraction,
        divider,
        inverse,
        leadBackground,
        trailingBackground,
        showDetail,
        master,
        detail,
    )
}

@Composable
fun MasterDetailView(
    splitView: Boolean,
    modifier: Modifier = Modifier,
    fraction: Float = 0.25f,
    divider: Boolean = false,
    inverse: Boolean = false,
    leadBackground: Color = Color.Transparent,
    trailingBackground: Color = Color.Transparent,
    showDetail: MutableState<Boolean>,
    master: @Composable BoxScope.(MutableState<Boolean>) -> Unit,
    detail: @Composable BoxScope.(MutableState<Boolean>) -> Unit,
) {
    MasterDetailView(
        splitView = splitView,
        modifier = modifier,
        fraction = fraction,
        divider = divider,
        inverse = inverse,
        leadBackground = leadBackground,
        trailingBackground = trailingBackground,
        showDetail = showDetail.value,
        enableBack = showDetail.value && !splitView,
        onClickBack = { showDetail.value = false },
        master = { master(showDetail) },
        detail = { detail(showDetail) },
    )
}

@Composable
fun MasterDetailView(
    splitView: Boolean,
    showDetail: Boolean,
    enableBack: Boolean,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier,
    fraction: Float = 0.25f,
    divider: Boolean = false,
    inverse: Boolean = false,
    leadBackground: Color = Color.Transparent,
    trailingBackground: Color = Color.Transparent,
    master: @Composable BoxScope.() -> Unit,
    detail: @Composable BoxScope.() -> Unit,
) {
    val masterContent = if (inverse) detail else master
    val detailContent = if (inverse) master else detail
    val maxWidthFraction = if (inverse) 1.0f - fraction else fraction
    if (splitView) {
        Row(modifier = modifier) {
            Box(
                modifier = Modifier.background(leadBackground).fillMaxWidth(maxWidthFraction).fillMaxHeight(),
                content = masterContent,
            )
            if (divider) {
                VerticalDivider()
            }
            Box(modifier = Modifier.background(trailingBackground).weight(1f).fillMaxSize(), content = detailContent)
        }
    } else {
        BackHandler(enableBack, onClickBack)
        Box(
            modifier =
                modifier.background(
                    if (showDetail) {
                        trailingBackground
                    } else {
                        leadBackground
                    }
                ),
            content = if (showDetail) detail else master,
        )
    }
}
