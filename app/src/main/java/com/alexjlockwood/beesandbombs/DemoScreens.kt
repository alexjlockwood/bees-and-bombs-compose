package com.alexjlockwood.beesandbombs

import android.os.Parcelable
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.android.parcel.Parcelize

sealed class DemoScreen : Parcelable {

    @Parcelize
    object DemoList : DemoScreen()

    @Immutable
    @Parcelize
    data class DemoDetails(val title: String) : DemoScreen()
}

@Composable
fun DemoList(onDemoSelected: (title: String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                backgroundColor = MaterialTheme.colors.surface,
            )
        }
    ) {
        LazyColumnFor(DemoRegistry.keys.sorted()) {
            ListItem(
                text = {
                    Text(
                        modifier = Modifier.preferredHeight(56.dp).wrapContentSize(Alignment.Center),
                        text = it,
                    )
                },
                modifier = Modifier.clickable { onDemoSelected.invoke(it) },
            )
        }
    }
}

@Composable
fun DemoDetails(demoDetails: DemoScreen.DemoDetails) {
    Box(modifier = Modifier.fillMaxSize(), alignment = Alignment.Center) {
        val modifier = Modifier.aspectRatio(1f).fillMaxSize().padding(16.dp)
        DemoRegistry.getValue(demoDetails.title)(modifier)
    }
}
