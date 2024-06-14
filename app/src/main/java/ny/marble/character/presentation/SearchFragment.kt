package ny.marble.character.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ny.marble.character.databinding.FragmentSearchBinding
import ny.marble.character.presentation.model.CharacterCardModel
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CardListAdapter({ id -> }, { offset ->
            viewModel.loadCharacters(offset)
        })

        binding.list.hasFixedSize()
        binding.list.adapter = adapter.apply {
            setHasStableIds(true)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            Timber.d("uiState : $state")
            lifecycleScope.launch {
                delay(0.2.seconds)
                binding.loading.root.isVisible = state is SearchUIState.Loading
            }

            if (state is SearchUIState.Success) {
                Timber.d("state Success : ${state.data}")
                adapter.submitList(state.data)
            }
        }


        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(keyword: Editable?) {
                viewModel.cancelRequest()
                keyword?.let {
                    if (it.length >= 2) {
                        lifecycleScope.launch {
                            delay(0.3.seconds)
                            viewModel.searchKeyword(it.toString())
                        }
                    }
                }
            }
        })
    }
}

sealed class SearchUIState {
    data object Idle : SearchUIState()
    data object Loading : SearchUIState()
    data class Success(val data: List<CharacterCardModel>) : SearchUIState()

    data object Fail : SearchUIState()
}