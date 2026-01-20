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

// --- MENU PRINCIPAL ---
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

// --- FORMULAIRE MANUEL ---
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

// --- INFO LIVRE ---
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