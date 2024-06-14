package ny.marble.character.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ny.marble.character.data.remote.CharacterApiService
import ny.marble.character.data.CharacterRepository
import ny.marble.character.data.auth.AuthDataSource
import ny.marble.character.data.remote.RemoteDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
class NetworkModule {
    val BASE_URL = "https://gateway.marvel.com:443/"

    @Provides
    fun provideHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }


    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient
    ) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit) : CharacterApiService {
        return retrofit.create(CharacterApiService::class.java)
    }

    @Provides
    fun provideRemoteDataSource(apiService: CharacterApiService) : RemoteDataSource {
        return RemoteDataSource(apiService)
    }

    @Provides
    fun providerRepository(
        remoteDataSource: RemoteDataSource
    ) : CharacterRepository {
        return CharacterRepository(remoteDataSource, AuthDataSource())
    }
}