package fr.enssat.sharemybook.BastienLucasZakaria.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val isbn: String,
    val title: String,
    val author: String
)