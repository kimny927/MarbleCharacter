package ny.marble.character.data.remote

import ny.marble.character.data.model.CharacterDataWrapperApiModel

class RemoteDataSource(
    private val apiService: CharacterApiService,
) {
    suspend fun getCharacters(
        timeStamp: Long,
        publicKey: String,
        hashValue: String,
        offset: Int,
        limit: Int
    ): CharacterDataWrapperApiModel = apiService.getCharacters(
        timeStamp = timeStamp,
        publicKey = publicKey,
        hashValue = hashValue,
        offset = offset,
        limit = limit
    )

    suspend fun getCharactersBySearching(
        timeStamp: Long,
        publicKey: String,
        hashValue: String,
        keyword: String,
        offset: Int,
        limit: Int
    ): CharacterDataWrapperApiModel = apiService.getCharactersBySearching(
        timeStamp = timeStamp,
        publicKey = publicKey,
        hashValue = hashValue,
        keyword = keyword,
        offset = offset,
        limit = limit
    )

}