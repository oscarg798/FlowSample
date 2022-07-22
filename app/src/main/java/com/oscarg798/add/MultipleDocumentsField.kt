package com.oscarg798

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MultipleDocsField(
    documents: List<String>,
    onDelete: (String) -> Unit,
    onAddPressed: () -> Unit,
    modifier: Modifier = Modifier,
    collapseMode: Boolean = false
) {
    Column(modifier) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            documents.getDocuments(collapseMode).forEach { document ->
                Row() {
                    Text(text = document, modifier = Modifier.weight(.8f))
                    IconButton(onClick = {
                        onDelete(document)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "delete"
                        )
                    }
                }
            }
        }

        Button(
            onClick = onAddPressed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add")
        }
    }

}

private fun List<String>.getDocuments(collapseMode: Boolean): List<String> {
    return if (collapseMode) {
        take(2)
    } else {
        this
    }
}