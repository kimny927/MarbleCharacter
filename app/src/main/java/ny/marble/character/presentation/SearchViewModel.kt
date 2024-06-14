package ny.marble.character.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ny.marble.character.data.CharacterRepository
import ny.marble.character.data.remote.ApiResult
import ny.marble.character.data.remote.onError
import ny.marble.character.data.remote.onException
import ny.marble.character.data.remote.onLoading
import ny.marble.character.data.remote.onSuccess
import ny.marble.character.presentation.model.CharacterCardModel
import ny.marble.character.presentation.model.CharacterCardWrapperModel
import timber.log.Timber
import javax.inject.Inject

const val INITIAL_SEARCH_KEYWORD = "ALL_CHARACTERS_**%%!!"
const val INITIAL_TOTAL_COUNT = -1

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    /**
     * 키워드 검색 결과 총 개수
     */
    private var _totalCount: Int = INITIAL_TOTAL_COUNT

    /**
     * 이전에 불러온 검색 결과 리스트
     */
    private val _cardList = mutableListOf<CharacterCardModel>()


    private val _uiState: MutableStateFlow<SearchUIState> = MutableStateFlow(SearchUIState.Idle)
    val uiState: LiveData<SearchUIState> = _uiState.asLiveData()

    private val keywordChannel = Channel<String>()
    private val offsetChannel = Channel<Int>()

    private val requestChannel = Channel<SearchRequest>()

    private var requestJob: Job? = null

    init {
        //load all characters
        sendKeyword(INITIAL_SEARCH_KEYWORD)
        sendOffset(0)

        viewModelScope.launch(Dispatchers.Default) {
            keywordChannel.consumeAsFlow()
                .combine(offsetChannel.consumeAsFlow()) { keyword, offset ->
                    Timber.d("keyword: $keyword, offset: $offset")
                    val event = if (keyword == INITIAL_SEARCH_KEYWORD) {
                        SearchRequest.GetCharacterRequest(offset)
                    } else {
                        SearchRequest.SearchCharacterRequest(offset = offset, keyword = keyword)
                    }
                    event
                }.collectLatest {
                    requestChannel.send(it)
                }
        }

        viewModelScope.launch(Dispatchers.Default) {
            repository.characters.collectLatest {
                handleResult(it)
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            requestChannel.consumeAsFlow().collectLatest {
                requestJob = launch {
                    withContext(Dispatchers.IO) {
                        when (it) {
                            is SearchRequest.GetCharacterRequest -> repository.getCharactersFlow(it.offset)
                            is SearchRequest.SearchCharacterRequest -> repository.getCharactersBySearchingFlow(
                                keyword = it.keyword,
                                offset = it.offset
                            )
                        }
                    }

                }
            }
        }

    }

    private fun initialPreviousData() {
        _totalCount = INITIAL_TOTAL_COUNT
        _cardList.clear()
    }

    fun loadCharacters(offset: Int) {
        sendOffset(offset)
    }

    private  fun sendKeyword(keyword: String) {
        viewModelScope.launch (Dispatchers.Default) {
            keywordChannel.send(keyword)
        }
    }

    private fun sendOffset(offset: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            if (_totalCount != _cardList.size) {
                offsetChannel.send(offset)
            }
        }
    }

    fun searchKeyword(keyword: String) {
        viewModelScope.launch {
            val initialJob = launch(Dispatchers.Default) {
                initialPreviousData()
            }
            initialJob.join()
            sendKeyword(keyword)
            sendOffset(0)
        }

    }

    fun cancelRequest() {
        requestJob?.cancel()
        requestJob = null
    }

    private fun handleResult(result: ApiResult<CharacterCardWrapperModel>) {
        result.onLoading {
            Timber.d("onLoading")
            _uiState.value = SearchUIState.Loading
        }.onSuccess { data ->
            Timber.d("onSuccess")
            _cardList.addAll(data.cardList)
            _totalCount = data.totalCount
            _uiState.value = SearchUIState.Success(
                data = _cardList.toMutableList()
            )
        }.onError { code, message ->
            _uiState.value = SearchUIState.Fail
            Timber.d("onError, $code, $message")
        }.onException {
            _uiState.value = SearchUIState.Fail
            Timber.d("onException, $it")
        }
    }

    override fun onCleared() {
        super.onCleared()
        keywordChannel.close()
        offsetChannel.close()
        requestChannel.close()
    }

}

sealed class SearchRequest {
    data class GetCharacterRequest(val offset: Int) : SearchRequest()
    data class SearchCharacterRequest(val keyword: String, val offset: Int) : SearchRequest()
}