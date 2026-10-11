package com.example.onestopfootball

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.onestopfootball.data.auth.AuthRepository
import com.example.onestopfootball.data.local.AppDatabase
import com.example.onestopfootball.ui.account.AccountScreen
import com.example.onestopfootball.ui.login.LoginScreen
import com.example.onestopfootball.ui.register.RegisterScreen
import com.example.onestopfootball.ui.theme.OneStopFootballTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OneStopFootballTheme {
                OneStopFootballApp()
            }
        }
    }
}

/** Routes used by the application. */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Leagues : Screen("leagues")
    object Matches : Screen("matches")
    object Favorites : Screen("favorites")
    object Account : Screen("account")
    object Login : Screen("login")
    object Register : Screen("register")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneStopFootballApp() {
    val navController = rememberNavController()

    // Room-backed repository shared by Login and Register screens.
    val appContext = LocalContext.current.applicationContext
    val userDao = remember(appContext) {
        AppDatabase.getInstance(appContext).userDao()
    }
    val authRepository = remember(userDao) {
        AuthRepository(userDao)
    }

    // Null means guest; non-null means logged in during this app session.
    var loggedInUsername by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = setOf(
        Screen.Home.route,
        Screen.Leagues.route,
        Screen.Matches.route,
        Screen.Favorites.route
    )
    val authenticationRoutes = setOf(
        Screen.Login.route,
        Screen.Register.route
    )

    Scaffold(
        topBar = {
            if (currentRoute !in authenticationRoutes) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                Screen.Home.route -> "One Stop Football"
                                Screen.Leagues.route -> "Leagues"
                                Screen.Matches.route -> "Match Center"
                                Screen.Favorites.route -> "Favorites"
                                Screen.Account.route -> "Account"
                                else -> "One Stop Football"
                            }
                        )
                    },
                    actions = {
                        if (currentRoute != Screen.Account.route) {
                            TextButton(
                                onClick = {
                                    navController.navigate(Screen.Account.route) {
                                        launchSingleTop = true
                                    }
                                }
                            ) {
                                Text("Account")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute in mainRoutes) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                DashboardScreen(
                    username = loggedInUsername,
                    onOpenLeagues = {
                        navController.navigate(Screen.Leagues.route)
                    },
                    onOpenMatches = {
                        navController.navigate(Screen.Matches.route)
                    }
                )
            }

            composable(Screen.Leagues.route) {
                LeaguesScreen()
            }

            composable(Screen.Matches.route) {
                MatchesScreen()
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    isLoggedIn = loggedInUsername != null,
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            composable(Screen.Account.route) {
                AccountScreen(
                    username = loggedInUsername,
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLogoutClick = {
                        loggedInUsername = null
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    authRepository = authRepository,
                    onBack = {
                        navController.popBackStack()
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = { user ->
                        loggedInUsername = user.username
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    authRepository = authRepository,
                    onBack = {
                        navController.popBackStack()
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onRegisterSuccess = { user ->
                        // Successful registration signs the new user in for this session.
                        loggedInUsername = user.username
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("🏠") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Leagues.route,
            onClick = {
                navController.navigate(Screen.Leagues.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("🌍") },
            label = { Text("Leagues") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Matches.route,
            onClick = {
                navController.navigate(Screen.Matches.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("⚽") },
            label = { Text("Matches") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Favorites.route,
            onClick = {
                navController.navigate(Screen.Favorites.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("❤️") },
            label = { Text("Favorites") }
        )
    }
}

@Composable
fun DashboardScreen(
    username: String?,
    onOpenLeagues: () -> Unit,
    onOpenMatches: () -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (username.isNullOrBlank()) {
                    "Welcome, Football Fan!"
                } else {
                    "Halo, $username!"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Everything about football in one place.")
        }

        item {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search football") },
                placeholder = { Text("Club, league, news...") },
                singleLine = true
            )
        }

        item {
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenLeagues
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🌍 Leagues", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Explore football leagues and competitions.")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenMatches
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚽ Match Center", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Check upcoming and finished matches.")
                }
            }
        }

        item {
            Text(
                text = "Latest News",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📰 Latest Football News", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Football news will be loaded from the News API.")
                }
            }
        }

        item {
            Text(
                text = "Upcoming Matches",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Manchester City vs Arsenal", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upcoming Match")
                }
            }
        }
    }
}

@Composable
fun LeaguesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Football Leagues",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("🇬🇧 Premier League")
        Spacer(modifier = Modifier.height(12.dp))
        Text("🇪🇸 La Liga")
        Spacer(modifier = Modifier.height(12.dp))
        Text("🇮🇹 Serie A")
        Spacer(modifier = Modifier.height(12.dp))
        Text("🇩🇪 Bundesliga")
        Spacer(modifier = Modifier.height(12.dp))
        Text("🇫🇷 Ligue 1")
    }
}

@Composable
fun MatchesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Match Center",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Manchester City vs Liverpool", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upcoming")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Barcelona vs Real Madrid", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upcoming")
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoggedIn) {
            Text(
                text = "My Favorite Clubs",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("Favorite clubs will appear here.")
        } else {
            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("You can browse One Stop Football without logging in.")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Login is required to save favorite clubs.")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onLoginClick) {
                Text("Login")
            }
        }
    }
}
