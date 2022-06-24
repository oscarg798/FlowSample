package com.oscarg798.add.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.oscarg798.R
import com.oscarg798.add.AddViewModel
import com.oscarg798.add.FlowPage
import com.oscarg798.ui.theme.PageBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailFragment : Fragment() {

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
                    Modifier
                        .background(PageBackground)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column {

                        OutlinedTextField(value = state.email, onValueChange = {
                            viewModel.onEmailChange(it)
                        }, placeholder = {
                            Text(text = stringResource(R.string.email_hint))

                        }, modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}