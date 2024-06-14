package ny.marble.character.data.remote

import ny.marble.character.data.model.CharacterDataWrapperApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApiService {
    @GET("v1/public/characters")
    suspend fun getCharactersBySearching(
        @Query("ts") timeStamp: Long,
        @Query("apikey") publicKey: String,
        @Query("hash") hashValue: String,
        @Query("nameStartsWith") keyword: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CharacterDataWrapperApiModel

    @GET("v1/public/characters")
    suspend fun getCharacters(
        @Query("ts") timeStamp: Long,
        @Query("apikey") publicKey: String,
        @Query("hash") hashValue: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CharacterDataWrapperApiModel
}