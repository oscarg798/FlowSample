package com.oscarg798.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.R

@Composable
fun FlowPage(
    modifier: Modifier = Modifier,
    showSwipeLabel: Boolean = true,
    content: @Composable () -> Unit
) {

    ConstraintLayout(
        modifier
            .padding(16.dp)
    ) {
        val (contentRef, swipeLabel) = createRefs()

        Box(
            Modifier
                .fillMaxWidth()
                .constrainAs(contentRef) {
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                    linkTo(parent.start, parent.end)
                    if (showSwipeLabel) {
                        bottom.linkTo(swipeLabel.top)
                    } else {
                        bottom.linkTo(parent.bottom)
                    }
                    top.linkTo(parent.top)
                }
                .padding(bottom = 8.dp)
        ) {
            content()
        }

        if (showSwipeLabel) {
            Text(
                text = stringResource(R.string.swipe_to_continue),
                modifier = Modifier
                    .constrainAs(swipeLabel) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(bottom = 16.dp),
                style = TextStyle(color = LinkColor, fontWeight = FontWeight.Bold)
            )
        }


    }
}

private val LinkColor = Color(0xFF2979FF)