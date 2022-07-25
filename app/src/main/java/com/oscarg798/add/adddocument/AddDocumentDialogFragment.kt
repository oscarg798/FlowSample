package com.oscarg798.add.adddocument

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarg798.MultipleDocsField
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider


@AndroidEntryPoint
class AddDocumentDialogFragment : DialogFragment() {

    @Inject
    lateinit var addDocumentsViewModelFactory: AddDocumentsViewModel.AddDocumentsViewModelFactory

    private val viewModel: AddDocumentsViewModel by viewModels {
        ViewModelFactory.from {
            addDocumentsViewModelFactory.create(requireArguments().getParcelable("DOCUMENTS")!!)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        );

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsState(initial = AddDocumentsViewModel.State())

                setFragmentResult(
                    "DOCUMENTS", bundleOf(
                        "DOCUMENTS" to DocumentData(
                            state.documents,
                            state.fieldId
                        )
                    )
                )

                MultipleDocsField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    documents = state.documents,
                    onDelete = {
                        viewModel.onDeleteClicked(it)
                    },
                    onAddPressed = {
                        viewModel.onAddClicked()
                    })
            }
        }
    }
}

class ViewModelFactory<T : ViewModel> @Inject constructor(
    private val provider: Provider<T>
) : ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return provider.get() as T
    }

    companion object {
        @JvmStatic
        inline fun <T : ViewModel> from(crossinline provider: () -> T): ViewModelFactory<T> {
            return ViewModelFactory { provider.invoke() }
        }
    }
}
