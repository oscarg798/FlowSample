package com.oscarg798.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.oscarg798.R
import com.oscarg798.shimmer
import com.oscarg798.ui.theme.Background
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed

                val state by viewModel.state.collectAsState(initial = ListViewModel.State())
                val events by viewModel.event.collectAsState(initial = null)

                LaunchedEffect(key1 = events) {
                    val event = events ?: return@LaunchedEffect

                    if (event is ListViewModel.Event.NavigateToAdd) {
                        findNavController().navigate(R.id.action_listFragment_to_addFragment)
                    }
                }

                Scaffold(
                    floatingActionButton = {
                        if (!state.loading) {
                            FloatingActionButton(onClick = {
                                viewModel.onAddPressed()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_add),
                                    contentDescription = "Add"
                                )
                            }
                        }
                    },
                    backgroundColor = Background
                ) {
                    if (state.loading) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(5) {
                                Card(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .shimmer()
                                ) {
                                }
                            }
                        }
                    }

                    val people = state.people ?: return@Scaffold

                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
                        onRefresh = { viewModel.refresh() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(people, key = { people ->
                                people.id
                            }) { person ->
                                Card(
                                    Modifier
                                        .fillMaxWidth()
                                ) {
                                    Column(
                                        Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(text = "${person.name.capitalize()} ${person.lastname.capitalize()}")

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = person.email,
                                            style = TextStyle(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}