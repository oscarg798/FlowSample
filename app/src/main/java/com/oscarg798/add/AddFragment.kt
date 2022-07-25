package com.oscarg798.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.oscarg798.MultipleDocsField
import com.oscarg798.R
import com.oscarg798.add.adddocument.DocumentData
import com.oscarg798.ui.theme.Background
import com.oscarg798.ui.theme.FlowSampleTheme
import com.oscarg798.ui.theme.PageBackground
import dagger.hilt.EntryPoints
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AddFragment : Fragment() {

    private val  viewModel: AddViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("DOCUMENTS") { key, bundle ->
            if(key == "DOCUMENTS" && bundle.containsKey("DOCUMENTS")){
                viewModel.onDocumentsUpdated(bundle.getParcelable("DOCUMENTS")!!)
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FlowSampleTheme {
                    val state by viewModel.state.collectAsState(initial = AddViewModel.State())
                    val events by viewModel.event.collectAsState(initial = null)

                    val pagerState = rememberPagerState()

                    LaunchedEffect(pagerState) {
                        // Collect from the pager state a snapshotFlow reading the currentPage
                        snapshotFlow { pagerState.currentPage }.collect { page ->
                            viewModel.onPageScrolled(page)
                        }
                    }

                    LaunchedEffect(key1 = events) {
                        val event = events ?: return@LaunchedEffect

                        when (event) {
                            is AddViewModel.Event.ReturnToList -> {
                                findNavController().popBackStack()
                            }
                            is AddViewModel.Event.SwipeBack -> {
                                pagerState.scrollToPage(pagerState.currentPage - 1)
                            }
                            is AddViewModel.Event.OpenAdd -> {
                                findNavController().navigate(R.id.addDocuments, args = bundleOf(
                                    "DOCUMENTS" to DocumentData(
                                        documents = event.documents,
                                        fieldId = event.fieldId,

                                    )
                                ), navOptions {
                                    launchSingleTop = false
                                })
                            }
                            AddViewModel.Event.None -> {}
                        }
                    }

                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed


                    Scaffold(
                    ) {
                        HorizontalPager(
                            count = 3,
                            contentPadding = PaddingValues(16.dp),
                            itemSpacing = 16.dp,
                            state = pagerState,
                            modifier = Modifier.background(Background)
                        ) { page ->
                            when (page) {
                                0 -> FlowPageOne(state = state, onNameChange = {
                                    viewModel.onNameChange(it)
                                }, onLastNameChange = {
                                    viewModel.onLastNameChange(it)
                                }, onDocumentDeleted = { fieldId, document ->
                                    viewModel.onDeleteClicked(fieldId, document)
                                }, onAddDocument = {
                                    viewModel.onAddClicked(it)
                                })
                                1 -> FlowPageTwo(state) {
                                    viewModel.onEmailChange(it)
                                }
                                else -> FlowZero(state) {
                                    viewModel.simulateList(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun FlowZero(state: AddViewModel.State, onReady: (Int) -> Unit) {
    LaunchedEffect(key1 = true, block = {
        onReady(1)
    })

    val list = remember(state.lis) {
        mutableStateOf(state.lis)
    }

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                LayoutInflater.from(it).inflate(R.layout.fragment_add, null, false)
            }, modifier = Modifier
                .background(PageBackground)
                .weight(.8f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            it.findViewById<LinearLayout>(R.id.linear).apply {
                removeAllViews()

                if (list.value.isEmpty()) {
                    addView(ProgressBar(context))
                } else {
                    list.value.forEach {
                        addView(TextView(context).apply {
                            text = it
                        })
                    }
                }

            }

        }

        Button(
            onClick = {
                onReady(2)
            },
            Modifier.weight(.2f)
        ) {
            Text(text = "HitMe")
        }
    }


}


@Composable
private fun FlowPageOne(
    state: AddViewModel.State,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDocumentDeleted: (fieldId: String, document: String) -> Unit,
    onAddDocument: (String) -> Unit
) {

    FlowPage(
        Modifier
            .background(PageBackground)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {


            OutlinedTextField(value = state.name, onValueChange = {
                onNameChange(it)
            }, placeholder = {
                Text(text = stringResource(R.string.name_hint))

            },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.errors == AddViewModel.State.DataError.NameError
            )

            if (state.errors == AddViewModel.State.DataError.NameError) {
                Text(text = "Name should be fine")
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(value = state.lastName, onValueChange = {
                onLastNameChange(it)
            }, placeholder = {
                Text(text = stringResource(R.string.lastname_hint))

            },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.errors == AddViewModel.State.DataError.NameError
            )

            MultipleDocsField(documents = state.documents1, onDelete = {
                onDocumentDeleted(AddViewModel.field1Id, it)
            }, onAddPressed = {
                onAddDocument(AddViewModel.field1Id)
            }, collapseMode = true, modifier = Modifier.fillMaxWidth())

            MultipleDocsField(documents = state.documents2, onDelete = {
                onDocumentDeleted(AddViewModel.field2Id, it)
            }, onAddPressed = {
                onAddDocument(AddViewModel.field2Id)
            }, collapseMode = true, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FlowPageTwo(
    state: AddViewModel.State,
    onEmailChange: (String) -> Unit,
) {

    FlowPage(
        Modifier
            .background(PageBackground)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            OutlinedTextField(value = state.email, onValueChange = {
                onEmailChange(it)
            }, placeholder = {
                Text(text = stringResource(R.string.email_hint))

            }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

