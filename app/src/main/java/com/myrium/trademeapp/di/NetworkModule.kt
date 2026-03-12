package com.myrium.trademeapp.di

import com.google.gson.Gson
import com.myrium.trademeapp.BuildConfig
import com.myrium.trademeapp.network.ListingsApi
import com.myrium.trademeapp.network.LocalListingsInterceptor
import com.myrium.trademeapp.network.OAuth1PlaintextInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Named("oauthConsumerKey")
    fun provideOAuthConsumerKey(): String = BuildConfig.TRADE_ME_CONSUMER_KEY

    @Provides
    @Named("oauthConsumerSecret")
    fun provideOAuthConsumerSecret(): String = BuildConfig.TRADE_ME_CONSUMER_SECRET

    @Provides
    @Singleton
    fun provideOAuth1PlaintextInterceptor(
        @Named("oauthConsumerKey") consumerKey: String,
        @Named("oauthConsumerSecret") consumerSecret: String,
    ): OAuth1PlaintextInterceptor {
        return OAuth1PlaintextInterceptor(
            consumerKey = consumerKey,
            consumerSecret = consumerSecret,
        )
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        localListingsInterceptor: LocalListingsInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        oAuth1PlaintextInterceptor: OAuth1PlaintextInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(oAuth1PlaintextInterceptor)
//            .addInterceptor(localListingsInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.tmsandbox.co.nz/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideListingsApi(retrofit: Retrofit): ListingsApi {
        return retrofit.create(ListingsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}
