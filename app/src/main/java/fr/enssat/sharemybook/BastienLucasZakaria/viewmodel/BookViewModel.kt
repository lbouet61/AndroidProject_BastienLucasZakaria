package fr.enssat.sharemybook.BastienLucasZakaria.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.enssat.sharemybook.BastienLucasZakaria.data.AppDatabase
import fr.enssat.sharemybook.BastienLucasZakaria.data.Book
import fr.enssat.sharemybook.BastienLucasZakaria.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    // Liste observable pour l'UI
    val books = mutableStateListOf<Book>()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            val list = db.bookDao().getAllBooks()
            books.clear()
            books.addAll(list)
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            db.bookDao().insertBook(book)
            loadBooks() // Rafraichir la liste
        }
    }

    // Récupération depuis OpenLibrary
    fun fetchBookFromApi(isbn: String, onSuccess: (Book) -> Unit, onError: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getBookInfo("ISBN:$isbn")
                val key = "ISBN:$isbn"
                // Parsing manuel simple du JSON
                val jsonObject = response.asJsonObject
                if (jsonObject.has(key)) {
                    val bookData = jsonObject.getAsJsonObject(key)
                    val title = bookData.get("title").asString
                    // Les auteurs sont dans un tableau
                    val authorsArray = bookData.getAsJsonArray("authors")
                    val author = if (authorsArray.size() > 0) authorsArray[0].asJsonObject.get("name").asString else "Inconnu"

                    val newBook = Book(isbn, title, author)
                    // On retourne sur le Thread Principal
                    viewModelScope.launch { onSuccess(newBook) }
                } else {
                    viewModelScope.launch { onError() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelScope.launch { onError() }
            }
        }
    }

    // Helper pour trouver un livre dans la liste actuelle par ISBN
    fun getBookFromList(isbn: String): Book? {
        return books.find { it.isbn == isbn }
    }
}