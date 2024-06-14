package ny.marble.character.data

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import ny.marble.character.data.auth.AuthDataSource
import ny.marble.character.data.remote.ApiResult
import ny.marble.character.data.remote.RemoteDataSource
import ny.marble.character.data.remote.handleApi
import ny.marble.character.presentation.model.CharacterCardWrapperModel
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val authDataSource: AuthDataSource
) {
    private val characterChannel: Channel<ApiResult<CharacterCardWrapperModel>> = Channel()
    val characters: Flow<ApiResult<CharacterCardWrapperModel>> = characterChannel.consumeAsFlow()

    suspend fun getCharactersFlow(
        offset: Int,
        limit: Int = 10
    ) {
        val (timeStamp, publicKey, hashValue) = authDataSource.getMarbleAuthorizingValue()
        characterChannel.send(ApiResult.Loading)
        characterChannel.send(
            handleApi {
                remoteDataSource.getCharacters(
                    timeStamp = timeStamp,
                    publicKey = publicKey,
                    hashValue = hashValue,
                    offset = offset,
                    limit = limit
                ).toModel()
            }
        )
    }

    suspend fun getCharactersBySearchingFlow(
        keyword: String,
        offset: Int,
        limit: Int = 10
    ) {

        val (timeStamp, publicKey, hashValue) = authDataSource.getMarbleAuthorizingValue()
        characterChannel.send(ApiResult.Loading)
        characterChannel.trySend(
            handleApi {
                remoteDataSource.getCharactersBySearching(
                    timeStamp = timeStamp,
                    publicKey = publicKey,
                    hashValue = hashValue,
                    keyword = keyword,
                    offset = offset,
                    limit = limit
                ).toModel()
            }
        )

    }

}