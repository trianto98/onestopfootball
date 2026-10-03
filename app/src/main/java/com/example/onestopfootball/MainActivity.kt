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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.onestopfootball.ui.theme.OneStopFootballTheme
import com.example.onestopfootball.ui.login.LoginScreen
import com.example.onestopfootball.ui.account.AccountScreen


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


/*
 * Semua screen yang digunakan dalam aplikasi.
 */
sealed class Screen(val route: String) {

    object Home : Screen("home")

    object Leagues : Screen("leagues")

    object Matches : Screen("matches")

    object Favorites : Screen("favorites")

    object Account : Screen("account")

    object Login : Screen("login")
}


/*
 * Root aplikasi.
 * Di sinilah Navigation dan login state kita kelola.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneStopFootballApp() {

    val navController = rememberNavController()

    /*
     * Untuk sementara login masih demo/local.
     * Nanti bisa kita ganti dengan authentication sungguhan.
     */
    var isLoggedIn by rememberSaveable {

        mutableStateOf(false)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry
        ?.destination
        ?.route

    val mainRoutes = setOf(

        Screen.Home.route,
        Screen.Leagues.route,
        Screen.Matches.route,
        Screen.Favorites.route
    )

    Scaffold(

        topBar = {

            if (currentRoute != Screen.Login.route) {

                TopAppBar(

                    title = {

                        Text(
                            text = when (currentRoute) {

                                Screen.Home.route ->
                                    "One Stop Football"

                                Screen.Leagues.route ->
                                    "Leagues"

                                Screen.Matches.route ->
                                    "Match Center"

                                Screen.Favorites.route ->
                                    "Favorites"

                                Screen.Account.route ->
                                    "Account"

                                else ->
                                    "One Stop Football"
                            }
                        )
                    },

                    actions = {

                        if (currentRoute != Screen.Account.route) {

                            TextButton(
                                onClick = {

                                    navController.navigate(
                                        Screen.Account.route
                                    )
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

            /*
             * HOME
             */
            composable(Screen.Home.route) {

                DashboardScreen(

                    onOpenLeagues = {

                        navController.navigate(
                            Screen.Leagues.route
                        )
                    },

                    onOpenMatches = {

                        navController.navigate(
                            Screen.Matches.route
                        )
                    }
                )
            }


            /*
             * LEAGUES
             */
            composable(Screen.Leagues.route) {

                LeaguesScreen()
            }


            /*
             * MATCHES
             */
            composable(Screen.Matches.route) {

                MatchesScreen()
            }


            /*
             * FAVORITES
             */
            composable(Screen.Favorites.route) {

                FavoritesScreen(

                    isLoggedIn = isLoggedIn,

                    onLoginClick = {

                        navController.navigate(
                            Screen.Login.route
                        )
                    }
                )
            }


            /*
             * ACCOUNT
             */
            composable(Screen.Account.route) {

                AccountScreen(

                    isLoggedIn = isLoggedIn,

                    onLoginClick = {

                        navController.navigate(
                            Screen.Login.route
                        )
                    },

                    onLogoutClick = {

                        isLoggedIn = false
                    }
                )
            }


            /*
             * LOGIN
             */
            composable(Screen.Login.route) {

                LoginScreen(

                    onBack = {

                        navController.popBackStack()
                    },

                    onLoginSuccess = {

                        isLoggedIn = true

                        navController.navigate(
                            Screen.Account.route
                        ) {

                            popUpTo(
                                Screen.Login.route
                            ) {

                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}


/*
 * Bottom Navigation
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {

    NavigationBar {

        NavigationBarItem(

            selected = currentRoute == Screen.Home.route,

            onClick = {

                navController.navigate(
                    Screen.Home.route
                ) {

                    popUpTo(
                        Screen.Home.route
                    ) {

                        saveState = true
                    }

                    launchSingleTop = true

                    restoreState = true
                }
            },

            /*
             * NavigationBarItem wajib memiliki icon.
             * Untuk tahap awal kita gunakan emoji.
             */
            icon = {

                Text("🏠")
            },

            label = {

                Text("Home")
            }
        )


        NavigationBarItem(

            selected = currentRoute == Screen.Leagues.route,

            onClick = {

                navController.navigate(
                    Screen.Leagues.route
                ) {

                    popUpTo(
                        Screen.Home.route
                    ) {

                        saveState = true
                    }

                    launchSingleTop = true

                    restoreState = true
                }
            },

            icon = {

                Text("🌍")
            },

            label = {

                Text("Leagues")
            }
        )


        NavigationBarItem(

            selected = currentRoute == Screen.Matches.route,

            onClick = {

                navController.navigate(
                    Screen.Matches.route
                ) {

                    popUpTo(
                        Screen.Home.route
                    ) {

                        saveState = true
                    }

                    launchSingleTop = true

                    restoreState = true
                }
            },

            icon = {

                Text("⚽")
            },

            label = {

                Text("Matches")
            }
        )


        NavigationBarItem(

            selected = currentRoute == Screen.Favorites.route,

            onClick = {

                navController.navigate(
                    Screen.Favorites.route
                ) {

                    popUpTo(
                        Screen.Home.route
                    ) {

                        saveState = true
                    }

                    launchSingleTop = true

                    restoreState = true
                }
            },

            icon = {

                Text("❤️")
            },

            label = {

                Text("Favorites")
            }
        )
    }
}


/*
 * HOME / DASHBOARD
 */
@Composable
fun DashboardScreen(
    onOpenLeagues: () -> Unit,
    onOpenMatches: () -> Unit
) {

    var searchText by remember {

        mutableStateOf("")
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /*
         * Welcome
         */
        item {

            Text(
                text = "Welcome, Football Fan!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Everything about football in one place."
            )
        }


        /*
         * Search
         */
        item {

            OutlinedTextField(

                value = searchText,

                onValueChange = {

                    searchText = it
                },

                modifier = Modifier.fillMaxWidth(),

                label = {

                    Text("Search football")
                },

                placeholder = {

                    Text("Club, league, news...")
                },

                singleLine = true
            )
        }


        /*
         * Quick Access
         */
        item {

            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }


        /*
         * League
         */
        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                onClick = onOpenLeagues
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "🌍 Leagues",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Explore football leagues and competitions."
                    )
                }
            }
        }


        /*
         * Match
         */
        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                onClick = onOpenMatches
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "⚽ Match Center",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Check upcoming and finished matches."
                    )
                }
            }
        }


        /*
         * News
         */
        item {

            Text(
                text = "Latest News",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }


        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "📰 Latest Football News",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Football news will be loaded from the News API."
                    )
                }
            }
        }


        /*
         * Match Preview
         */
        item {

            Text(
                text = "Upcoming Matches",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }


        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Manchester City vs Arsenal",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Upcoming Match"
                    )
                }
            }
        }
    }
}


/*
 * LEAGUES
 */
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

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text("🇬🇧 Premier League")

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text("🇪🇸 La Liga")

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text("🇮🇹 Serie A")

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text("🇩🇪 Bundesliga")

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text("🇫🇷 Ligue 1")
    }
}


/*
 * MATCHES
 */
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

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Manchester City vs Liverpool",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text("Upcoming")
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Barcelona vs Real Madrid",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text("Upcoming")
            }
        }
    }
}


/*
 * FAVORITES
 */
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

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Favorite clubs will appear here."
            )

        } else {

            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "You can browse One Stop Football without logging in."
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Login is required to save favorite clubs."
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = onLoginClick
            ) {

                Text("Login")
            }
        }
    }
}


/*
 * ACCOUNT
 */
@Composable
fun AccountScreen(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        if (isLoggedIn) {

            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "demo@onestopfootball.com"
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            OutlinedButton(
                onClick = onLogoutClick
            ) {

                Text("Logout")
            }

        } else {

            Text(
                text = "Guest User",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "You can use One Stop Football without logging in."
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = onLoginClick
            ) {

                Text("Login")
            }
        }
    }
}


/*
 * LOGIN
 */
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    var email by rememberSaveable {

        mutableStateOf("")
    }

    var password by rememberSaveable {

        mutableStateOf("")
    }

    var errorMessage by rememberSaveable {

        mutableStateOf("")
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "One Stop Football",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Login to access your personal features."
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        /*
         * Email
         */
        OutlinedTextField(

            value = email,

            onValueChange = {

                email = it
                errorMessage = ""
            },

            modifier = Modifier.fillMaxWidth(),

            label = {

                Text("Email")
            },

            singleLine = true
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        /*
         * Password
         */
        OutlinedTextField(

            value = password,

            onValueChange = {

                password = it
                errorMessage = ""
            },

            modifier = Modifier.fillMaxWidth(),

            label = {

                Text("Password")
            },

            singleLine = true,

            visualTransformation =
                PasswordVisualTransformation()
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        Text(
            text = "Demo: demo@onestopfootball.com / 12345678",
            style = MaterialTheme.typography.bodySmall
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        /*
         * Error message
         */
        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }


        /*
         * Login button
         */
        Button(

            onClick = {

                if (
                    email == "demo@onestopfootball.com" &&
                    password == "12345678"
                ) {

                    onLoginSuccess()

                } else {

                    errorMessage =
                        "Email atau password salah."
                }
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Login")
        }


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        TextButton(
            onClick = onBack
        ) {

            Text("Back")
        }
    }
}