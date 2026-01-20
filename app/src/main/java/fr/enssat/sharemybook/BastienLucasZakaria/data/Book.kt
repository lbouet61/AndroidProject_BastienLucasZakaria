package fr.enssat.sharemybook.BastienLucasZakaria.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a book entity in the application's database.
 * This data class is used by Room to create the "books" table.
 *
 * @property isbn The International Standard Book Number, used as the primary key.
 * @property title The title of the book.
 * @property author The author of the book.
 */
@Entity(tableName = "books")
data class Book(
    @PrimaryKey val isbn: String,
    val title: String,
    val author: String
)