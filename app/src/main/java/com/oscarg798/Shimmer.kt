package com.oscarg798

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun Modifier.shimmer(): Modifier {
    return this.placeholder(
        visible = true,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.surface,
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = ShimmerColor
        )
    )
}

private val ShimmerColor = Color(
    0xffcccccc
)