package com.example.assignment6

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assignment6.ui.TimerViewModel
import com.example.assignment6.ui.clue.ClueScreen
import com.example.assignment6.ui.permissions.PermissionsScreen
import com.example.assignment6.ui.start.StartScreen
import com.example.assignment6.ui.theme.Assignment6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    TreasureHuntApp()
                }
            }
        }
    }
}

enum class TreasureHuntScreen(@StringRes val title: Int) {
    Permission(title = R.string.permissions_page), Start(title = R.string.start_page), Clue(title = R.string.clue_page), Solved(
        title = R.string.clue_solved_page
    ),
    Complete(title = R.string.treasure_hunt_completed_page)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreasureHuntAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    elapsedSeconds: Int,
    modifier: Modifier = Modifier,
) {
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title, style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = timeString, style = MaterialTheme.typography.bodyLarge
            )
        }
    }, modifier = modifier.border(width = 1.dp, color = Color.Gray), navigationIcon = {
        if (canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        }
    })
}

@Composable
fun TreasureHuntApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // get the title of the current screen
    var title = navController.currentBackStackEntryAsState().value?.destination?.route
    if (title == null) {
        title = ""
    }

    // determine the start route based off permission state
    var startDestination = TreasureHuntScreen.Start.name

    if (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        startDestination = TreasureHuntScreen.Permission.name
    }

    // initialize timer view model
    val timerViewModel: TimerViewModel = viewModel()
    val elapsedSeconds = timerViewModel.seconds.collectAsState(initial = 0)
    fun handleHuntStart() {
        navController.navigate(TreasureHuntScreen.Clue.name)
        timerViewModel.toggleTimer()
    }

    Scaffold(topBar = {
        TreasureHuntAppBar(title = title,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
            elapsedSeconds = elapsedSeconds.value
        )
    }) { innerPadding ->
        NavHost(
            navController = navController, startDestination = startDestination
        ) {
            composable(route = TreasureHuntScreen.Start.name) {
                StartScreen(
                    onStart = { handleHuntStart() }, modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(route = TreasureHuntScreen.Permission.name) {
                PermissionsScreen(
                    onPermissionsGranted = { navController.navigate(TreasureHuntScreen.Start.name) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(route = TreasureHuntScreen.Clue.name) {
                ClueScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Assignment6Theme {
        TreasureHuntApp()
    }
}
