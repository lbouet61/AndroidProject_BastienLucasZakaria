package fr.enssat.sharemybook.BastienLucasZakaria

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.enssat.sharemybook.BastienLucasZakaria.ui.screens.*
import fr.enssat.sharemybook.BastienLucasZakaria.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Pas de thème spécifique ici pour simplifier, utilise MaterialTheme par défaut
            val navController = rememberNavController()
            val bookViewModel: BookViewModel = viewModel() // ViewModel lié à l'activité

            NavHost(navController = navController, startDestination = "menu") {

                // 1. Menu Principal
                composable("menu") {
                    MenuPrincipalScreen(
                        viewModel = bookViewModel,
                        onNavigateToManualEntry = { navController.navigate("entry") },
                        onNavigateToScan = { navController.navigate("scan") },
                        onNavigateToInfo = { isbn -> navController.navigate("info/$isbn") }
                    )
                }

                // 2. Scan & Analyse
                composable("scan") {
                    AnalyseScreen(onIsbnFound = { isbn ->
                        // Quand un ISBN est trouvé, on appelle l'API
                        bookViewModel.fetchBookFromApi(
                            isbn = isbn,
                            onSuccess = { book ->
                                bookViewModel.addBook(book) // On ajoute en BDD
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "Livre trouvé : ${book.title}", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack() // Retour au menu
                                }
                            },
                            onError = {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "Livre introuvable pour ISBN: $isbn", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                        )
                    })
                }

                // 3. Entrée Manuelle
                composable("entry") {
                    BookEntryScreen(
                        viewModel = bookViewModel,
                        onBookAdded = { navController.popBackStack() }
                    )
                }

                // 4. Info Livre
                composable(
                    route = "info/{isbn}",
                    arguments = listOf(navArgument("isbn") { type = NavType.StringType })
                ) { backStackEntry ->
                    val isbn = backStackEntry.arguments?.getString("isbn") ?: ""
                    InfoLivreScreen(isbn = isbn, viewModel = bookViewModel, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}