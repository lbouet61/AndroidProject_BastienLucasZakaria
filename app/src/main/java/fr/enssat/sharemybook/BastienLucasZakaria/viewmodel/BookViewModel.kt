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

/**
 * ViewModel for managing book-related data.
 *
 * This ViewModel is responsible for interacting with the book data layer, which includes
 * a local Room database and a remote API (OpenLibrary). It provides methods to load, add,
 * and fetch book information, and exposes a list of books to the UI.
 *
 * @param application The application context, required by AndroidViewModel.
 */
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