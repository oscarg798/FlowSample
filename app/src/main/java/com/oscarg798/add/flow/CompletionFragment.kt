package com.oscarg798.add.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.oscarg798.R
import com.oscarg798.add.AddViewModel
import com.oscarg798.add.FlowPage
import com.oscarg798.ui.theme.PageBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletionFragment : Fragment() {

    private val viewModel: AddViewModel by viewModels(ownerProducer =  {
        requireParentFragment()
    })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed

                val state by viewModel.state.collectAsState(initial = AddViewModel.State())

                FlowPage(
                    showSwipeLabel = false,
                    modifier = Modifier
                        .background(PageBackground)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    ConstraintLayout(Modifier.fillMaxSize()) {

                        val (content, button) = createRefs()

                        Column(
                            Modifier
                                .padding(bottom = 16.dp)
                                .constrainAs(content) {
                                    linkTo(parent.start, parent.end)
                                    linkTo(parent.top, button.bottom)
                                    height = Dimension.fillToConstraints
                                    width = Dimension.fillToConstraints
                                }) {

                            Text(
                                text = stringResource(R.string.collect_title),
                                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row {
                                Text(
                                    text = "Name: ",
                                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(text = state.name.capitalize(), modifier = Modifier.fillMaxWidth())

                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Text(
                                    text = "Lastname: ",
                                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(text = state.lastName.capitalize(), modifier = Modifier.fillMaxWidth())

                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Text(
                                    text = "Email: ",
                                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(text = state.email.capitalize(), modifier = Modifier.fillMaxWidth())
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.addPeople()
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .constrainAs(button) {
                                    linkTo(parent.start, parent.end)
                                    bottom.linkTo(parent.bottom)
                                }
                        ) {
                            if(!state.loading){
                                Text(text = stringResource(R.string.submit_hint))
                            }else{
                                CircularProgressIndicator(
                                    color = Color.White
                                )
                            }
                        }
                    }


                }
            }
        }
    }
}