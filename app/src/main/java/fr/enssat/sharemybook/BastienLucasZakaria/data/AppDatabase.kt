package fr.enssat.sharemybook.BastienLucasZakaria.data

import android.content.Context
import androidx.room.*

/**
 * Data Access Object (DAO) for the Book entity.
 * This interface defines the database operations for managing books,
 * such as retrieving all books, finding a specific book by its ISBN,
 * and inserting a new book.
 */
@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>

    @Query("SELECT * FROM books WHERE isbn = :isbn")
    suspend fun getBookByIsbn(isbn: String): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)
}

/**
 * The main database class for the application.
 *
 * This class is an abstract class that extends [RoomDatabase]. It serves as the main access point
 * for the underlying connection to the app's persisted, relational data.
 * The class is annotated with `@Database`, listing the entities it contains ([Book]) and the database version.
 *
 * It provides an abstract method `bookDao()` for accessing the [BookDao] Data Access Object.
 *
 * This class follows the singleton pattern to ensure that only one instance of the database
 * is created throughout the application's lifecycle. The `getDatabase` companion object method
 * is used to retrieve this singleton instance.
 */
@Database(entities = [Book::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "sharemybook_db")
                    .build().also { INSTANCE = it }
            }
        }
    }
}