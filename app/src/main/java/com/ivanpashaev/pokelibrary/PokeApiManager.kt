package com.ivanpashaev.pokelibrary

import com.google.gson.annotations.SerializedName
import com.ivanpashaev.pokelibrary.PokeApiManager.PokemonListResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

class DataClasses {
    data class Pokemon(
        val id: Int,
        val name: String,
        val height: Int,
        val weight: Int,
        val sprites: Sprites,
        val types: List<TypeSlot>
    )

    data class Sprites(
        val front_default: String?,
        val other: OtherSprites?,
    )

    data class OtherSprites(
        @SerializedName("official-artwork")
        val officialArtwork: OfficialArtwork?
    )

    data class OfficialArtwork(
        @SerializedName("front_default")
        val frontDefault: String?
    )

    data class TypeSlot(
        val slot: Int,
        val type: Type,
    )

    data class Type(
        val name: String
    )
}

interface PokeApiService {
    @HEAD
    suspend fun checkConnection(): Response<Void>

    @GET("pokemon")
    suspend fun getPokemonList (
        @Query("limit") limit:Int = 20,
        @Query("offset") offset:Int = 0,
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(@Path("name") name: String): DataClasses.Pokemon
}

class PokeApiManager {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val serviceApi = retrofit.create(PokeApiService::class.java)

    suspend fun initConnection(): Boolean {
        try {
            val response = serviceApi.checkConnection()

            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun checkConnection(){

    }

    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): PokemonListResponse {
        return serviceApi.getPokemonList(limit,offset)
    }

    suspend fun getPokemonInfo(name: String): DataClasses.Pokemon {
        return serviceApi.getPokemonInfo(name)
    }

    data class PokemonListResponse(
        val results: List<PokemonEntry>
    )

    data class PokemonEntry(val name: String, val url: String)
}

