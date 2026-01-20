package fr.enssat.sharemybook.BastienLucasZakaria.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.enssat.sharemybook.BastienLucasZakaria.data.Book
import fr.enssat.sharemybook.BastienLucasZakaria.viewmodel.BookViewModel

/**
 * A Composable screen that serves as the main menu of the application.
 * It displays a list of the user's books and a Floating Action Button to add new ones.
 *
 * This screen features:
 * - A list of books from the [BookViewModel]. Each book is displayed in a [Card]
 *   and is clickable to navigate to the book's detail screen.
 * - A [FloatingActionButton] which, when clicked, opens an [AlertDialog].
 * - The [AlertDialog] offers two options for adding a book: "Scanner (Caméra)" or "Manuel".
 *   These options trigger the corresponding navigation callbacks.
 *
 * @param viewModel The [BookViewModel] instance containing the list of books and business logic.
 * @param onNavigateToManualEntry A callback function to navigate to the screen for manual book entry.
 * @param onNavigateToScan A callback function to navigate to the camera scanning screen.
 * @param onNavigateToInfo A callback function to navigate to the detail screen of a specific book, passing the book's ISBN.
 */// --- MENU PRINCIPAL ---
@Composable
fun MenuPrincipalScreen(
    viewModel: BookViewModel,
    onNavigateToManualEntry: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToInfo: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Mes Livres", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

            LazyColumn {
                items(viewModel.books) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onNavigateToInfo(book.isbn) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = book.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = book.author, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ajouter un livre") },
            text = { Text("Comment voulez-vous ajouter le livre ?") },
            confirmButton = {
                Button(onClick = { showDialog = false; onNavigateToScan() }) {
                    Text("Scanner (Caméra)")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false; onNavigateToManualEntry() }) {
                    Text("Manuel")
                }
            }
        )
    }
}

/**
 * A Composable screen that provides a form for manually adding a new book.
 * It includes text fields for the book's title, author, and ISBN.
 * A button allows the user to submit the new book, which is then added
 * to the `BookViewModel`.
 *
 * @param viewModel The view model `BookViewModel` that holds the application's data and business logic,
 *                  used here to add the new book.
 * @param onBookAdded A lambda function to be invoked when a book has been successfully added,
 *                    typically used for navigation (e.g., returning to the main list).
 */// FORMULAIRE MANUEL
@Composable
fun BookEntryScreen(
    viewModel: BookViewModel,
    onBookAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nouveau Livre", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        TextField(value = title, onValueChange = { title = it }, label = { Text("Titre") }, modifier = Modifier.fillMaxWidth())
        TextField(value = author, onValueChange = { author = it }, label = { Text("Auteur") }, modifier = Modifier.fillMaxWidth())
        TextField(value = isbn, onValueChange = { isbn = it }, label = { Text("ISBN") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if(isbn.isNotEmpty() && title.isNotEmpty()) {
                    viewModel.addBook(Book(isbn, title, author))
                    onBookAdded()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter")
        }
    }
}

/**
 * A Composable function that displays the detailed information of a specific book.
 * It retrieves the book's data from the ViewModel using its ISBN.
 *
 * @param isbn The ISBN of the book to display. This is used to find the book in the ViewModel.
 * @param viewModel The ViewModel instance containing the list of books and business logic.
 * @param onBack A lambda function to be invoked when the user wants to navigate back to the previous screen.
 */// --- INFO LIVRE ---
@Composable
fun InfoLivreScreen(
    isbn: String,
    viewModel: BookViewModel,
    onBack: () -> Unit
) {
    // On cherche le livre dans la liste du ViewModel
    val book = viewModel.getBookFromList(isbn)

    Column(modifier = Modifier.padding(16.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
        }
        Spacer(Modifier.height(16.dp))
        if (book != null) {
            Text("Titre : ${book.title}", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text("Auteur : ${book.author}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("ISBN : ${book.isbn}", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("Livre non trouvé")
        }
    }
}