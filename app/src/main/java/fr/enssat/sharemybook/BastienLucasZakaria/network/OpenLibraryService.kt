package fr.enssat.sharemybook.BastienLucasZakaria.network

import com.google.gson.JsonElement
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// On récupère un JsonElement brut car la clé change dynamiquement (ex: "ISBN:978...")
interface OpenLibraryService {
    @GET("api/books")
    suspend fun getBookInfo(
        @Query("bibkeys") isbn: String,
        @Query("jscmd") jscmd: String = "data",
        @Query("format") format: String = "json"
    ): JsonElement
}

object RetrofitInstance {
    val api: OpenLibraryService by lazy {
        Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryService::class.java)
    }
}